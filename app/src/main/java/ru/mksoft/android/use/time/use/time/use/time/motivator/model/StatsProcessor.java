package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class StatsProcessor {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private final MainActivity activity;
    private StatsProcessedUIListener uiListener;
    private boolean isProcessed;
    private final MainActivity.RequestPackageUsageStatsPermissionListener updateUseStatsRequestPackageUsageStatsPermissionListener;

    @Getter
    private DayProgress todayProgress = new DayProgress(0, 0);
    @Getter
    private DayProgress yesterdayProgress = new DayProgress(0, 0);

    /**
     * Constructor
     *
     * @param activity context of Activity
     */
    public StatsProcessor(MainActivity activity) {
        this.activity = activity;

        // Using a single listener to prevent duplicating permission requests at application startup
        updateUseStatsRequestPackageUsageStatsPermissionListener = isGranted -> {
            if (isGranted) {
                updateUseStatsGrantedPermission();
            } else {
                Log.w(LOG_TAG, "Package usage stats permission denied");
            }
        };
    }

    /**
     * Updates stats of all app begin with its last update
     */
    public void updateUseStats() {
        activity.requestPackageUsageStatsPermission(updateUseStatsRequestPackageUsageStatsPermissionListener);
    }

    private void updateUseStatsGrantedPermission() {
        isProcessed = false;
        List<UserApp> trackedUserApps = getTrackedUserApps();
        if (trackedUserApps.isEmpty()) {
            notifyStatsProcessed();
            return;
        }

        AppListParseResults appListParseResults = parseAppList(trackedUserApps);
        TreeMap<String, UserApp> userAppMap = appListParseResults.getUserAppMap();

        int strikeChange = 0;
        Calendar nextDate = Calendar.getInstance();
        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        for (Date curDate = appListParseResults.getMinDate(); curDate.before(today) || curDate.equals(today); ) {
            nextDate.setTime(curDate);
            nextDate.add(Calendar.DATE, 1);

            strikeChange += processDateStats(userAppMap, nextDate, curDate);

            curDate.setTime(nextDate.getTimeInMillis());
        }

        setUserAppsLastUpdate(trackedUserApps, today);
        try {
            DbHelperFactory.getHelper().getPropertyDAO().updateStrike(strikeChange);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        notifyStatsProcessed();
    }

    private void notifyStatsProcessed() {
        isProcessed = true;
        if (uiListener != null) {
            uiListener.processStatsProcessedBuilt();
        }
    }

    private static void setUserAppsLastUpdate(List<UserApp> trackedUserApps, Date today) {
        for (UserApp trackedUserApp : trackedUserApps) {
            trackedUserApp.setLastUpdateDate(today);
            try {
                DbHelperFactory.getHelper().getUserAppDAO().update(trackedUserApp);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int processDateStats(TreeMap<String, UserApp> userAppMap, Calendar nextDate, Date curDate) {
        HashMap<Category, Long> usedCategoriesStats = new HashMap<>();

        for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
            UserApp userApp = userAppMap.get(usageStat.getPackageName());
            if (userApp != null) {
                long usedTime = usageStat.getTotalTimeVisible();
                if (usedCategoriesStats.containsKey(userApp.getCategory())) {
                    usedCategoriesStats.replace(userApp.getCategory(), usedCategoriesStats.get(userApp.getCategory()) + usedTime);
                } else {
                    usedCategoriesStats.put(userApp.getCategory(), usedTime);
                }

                updateAppUseStatsForDate(curDate, usedTime, userApp);
                userAppMap.remove(userApp.getPackageName());
            }
        }

        processUnusedAppsForDate(userAppMap, curDate);
        if (curDate.equals(DateTimeUtils.getDateOfCurrentDayBegin())) {
            return 0;
        }

        return processCategoriesDateStats(usedCategoriesStats, curDate);
    }

    private int processCategoriesDateStats(HashMap<Category, Long> usedCategoriesStats, Date date) {
        int strikeChange = 0;
        for (Map.Entry<Category, Long> entry : usedCategoriesStats.entrySet()) {
            strikeChange += processCategoryDateState(entry.getKey(), entry.getValue(), date);
        }

        return strikeChange;
    }

    private int processCategoryDateState(Category category, Long usedTime, Date date) {
        boolean dayGoalCompleted = category.isDayGoalCompleted(date, usedTime);

        if (date.after(DateTimeUtils.getDateOtherDayBegin(-2))) {
            if (date.equals(DateTimeUtils.getDateOfCurrentDayBegin())) {
                todayProgress.plusTimeUsed(usedTime);
                if (!dayGoalCompleted) {
                    todayProgress.plusFailedGoal(1);
                }
            } else {
                yesterdayProgress.plusTimeUsed(usedTime);
                if (!dayGoalCompleted) {
                    yesterdayProgress.plusFailedGoal(1);
                }
            }
        }

        return dayGoalCompleted ? 1 : -1;
    }

    private static void processUnusedAppsForDate(TreeMap<String, UserApp> userAppMap, Date curDate) {
        for (Map.Entry<String, UserApp> entry : userAppMap.entrySet()) {
            updateAppUseStatsForDate(curDate, 0, entry.getValue());
        }
    }

    private static void updateAppUseStatsForDate(Date curDate, long totalTimeVisible, UserApp userApp) {
        if (curDate.equals(userApp.getLastUpdateDate())) {
            try {
                DbHelperFactory.getHelper().getAppUseStatsDao().removeAppStatsPerDay(userApp, curDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (curDate.after(userApp.getLastUpdateDate()) || curDate.equals(userApp.getLastUpdateDate())) {
            try {
                DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, totalTimeVisible, userApp));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<UsageStats> queryUsageStats(Date curDate, Calendar nextDate) {
        return ((UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE))
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, curDate.getTime(), nextDate.getTimeInMillis());
    }

    @NotNull
    private static AppUseStats createAppUseStatsForDate(Date curDate, long totalTimeVisible, UserApp userApp) {
        AppUseStats dbUseStats = new AppUseStats();
        dbUseStats.setDate(curDate);
        dbUseStats.setUsageTime(totalTimeVisible);
        dbUseStats.setDate(curDate);
        dbUseStats.setUserApp(userApp);

        return dbUseStats;
    }

    @NotNull
    private static List<UserApp> getTrackedUserApps() {
        List<UserApp> userApps = new ArrayList<>();
        try {
            userApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
        } catch (SQLException e) {
            //todo: корректно обработать ошибку
            e.printStackTrace();
        }

        return userApps;
    }

    private static AppListParseResults parseAppList(List<UserApp> userApps) {
        TreeMap<String, UserApp> userAppMap = new TreeMap<>();
        Date minDate = DateTimeUtils.getDateOfCurrentDayBegin();
        for (UserApp userApp : userApps) {
            userAppMap.put(userApp.getPackageName(), userApp);
            if (minDate.after(userApp.getLastUpdateDate())) {
                minDate = userApp.getLastUpdateDate();
            }
        }

        return new AppListParseResults(userAppMap, minDate);
    }

    /**
     * Add application statistic.
     *
     * @param userApp application
     */
    public void addAppStats(UserApp userApp) {
        activity.requestPackageUsageStatsPermission(isGranted -> {
            if (isGranted) {
                addAppStatsGrantedPermission(userApp);
            } else {
                Log.w(LOG_TAG, "Package usage stats permission denied");
            }
        });
    }

    private void addAppStatsGrantedPermission(UserApp userApp) {
        isProcessed = false;
        new Thread(() -> {
            Date curDate = DateTimeUtils.getDateOtherDayBegin(-Calendar.DAY_OF_WEEK);
            Calendar nextDate = Calendar.getInstance();
            Date today = DateTimeUtils.getDateOfCurrentDayBegin();
            while (curDate.before(today) || curDate.equals(today)) {
                nextDate.setTime(curDate);
                nextDate.add(Calendar.DATE, 1);
                addNewAppUsageStatsForDate(userApp, curDate, nextDate);
                curDate.setTime(nextDate.getTimeInMillis());
            }

            userApp.setLastUpdateDate(today);
            notifyStatsProcessed();
        }).start();
    }

    private void addNewAppUsageStatsForDate(UserApp userApp, Date curDate, Calendar nextDate) {
        for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
            if (userApp.getPackageName().equals(usageStat.getPackageName())) {
                try {
                    DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, usageStat.getTotalTimeVisible(), userApp));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return;
            }
        }

        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, 0, userApp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all usage statistics for the specific application
     *
     * @param userApp application for which you want to delete all usage statistics
     */
    public static void removeAllStatsForApp(UserApp userApp) {
        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().removeAppAllStats(userApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if all stats is processed and false otherwise
     *
     * @return is stats processing finish
     */
    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * If app list is building stats is not processed
     */
    public void processAppListBuildingBegan() {
        isProcessed = false;
    }

    /**
     * Subscribe single UI listener, who needs to react on stats processing finish.
     *
     * @param listener single UI listener, who needs to react on stats processing finish
     */
    public void subscribe(StatsProcessedUIListener listener) {
        uiListener = listener;
    }

    /**
     * Unsubscribe single UI listener, who needs to react on stats processing finish.
     */
    public void unsubscribeUIListener() {
        uiListener = null;
    }

    /**
     * UI element, which need to react on stats processing finish.
     */
    public interface StatsProcessedUIListener {
        /**
         * Calls when statistics is processed.
         */
        void processStatsProcessedBuilt();
    }

    @Getter
    private static class AppListParseResults {
        private final TreeMap<String, UserApp> userAppMap;
        private final Date minDate;

        public AppListParseResults(TreeMap<String, UserApp> userAppMap, Date minDate) {
            this.userAppMap = userAppMap;
            this.minDate = minDate;
        }
    }
}
