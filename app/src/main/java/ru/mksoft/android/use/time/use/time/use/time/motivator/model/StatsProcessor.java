package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private final Context context;
    private StatsProcessedListener uiListener;
    private boolean isProcessed;

    /**
     * Constructor
     *
     * @param context context of Activity
     */
    public StatsProcessor(Context context) {
        this.context = context;
    }

    /**
     * Updates stats of all app begin with its last update
     */
    public void updateUseStats() {
        isProcessed = false;
        List<UserApp> trackedUserApps = getTrackedUserApps();
        if (trackedUserApps.isEmpty()) {
            notifyStatsProcessed();
            return;
        }

        AppListParseResults appListParseResults = parseAppList(trackedUserApps);
        TreeMap<String, UserApp> userAppMap = appListParseResults.getUserAppMap();

        Calendar nextDate = Calendar.getInstance();
        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        for (Date curDate = appListParseResults.getMinDate(); curDate.before(today) || curDate.equals(today); ) {
            nextDate.setTime(curDate);
            nextDate.add(Calendar.DATE, 1);

            processDateStats(userAppMap, nextDate, curDate);

            curDate.setTime(nextDate.getTimeInMillis());
        }

        setUserAppsLastUpdate(trackedUserApps, today);
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

    private void processDateStats(TreeMap<String, UserApp> userAppMap, Calendar nextDate, Date curDate) {
        for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
            UserApp userApp = userAppMap.get(usageStat.getPackageName());
            if (userApp != null) {
                updateAppUseStatsForDate(curDate, usageStat.getTotalTimeVisible(), userApp);
                userAppMap.remove(userApp.getPackageName());
            }
        }

        for (Map.Entry<String, UserApp> entry : userAppMap.entrySet()) {
            updateAppUseStatsForDate(curDate, 0, entry.getValue());
        }
    }

    private static void updateAppUseStatsForDate(Date curDate, long totalTimeVisible, UserApp userApp) {
        if (curDate.equals(userApp.getLastUpdateDate())) {
            try {
                int deletedN = DbHelperFactory.getHelper().getAppUseStatsDao().removeAppStatsPerDay(userApp, curDate);
                System.out.println("adsskj;;adsj");
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
        List<UsageStats> usageStats = ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE))
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, curDate.getTime(), Calendar.getInstance().getTimeInMillis());

        return ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE))
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

    @Nullable
    private static List<UserApp> getTrackedUserApps() {
        List<UserApp> userApps = null;
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

    public void addAppStats(UserApp userApp) {
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

    public void removeAppAllStats(UserApp userApp) {
        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().removeAppAllStats(userApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public void subscribe(StatsProcessedListener listener) {
        uiListener = listener;
    }

    public void unsubscribeUIListener() {
        uiListener = null;
    }

    public interface StatsProcessedListener {
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
