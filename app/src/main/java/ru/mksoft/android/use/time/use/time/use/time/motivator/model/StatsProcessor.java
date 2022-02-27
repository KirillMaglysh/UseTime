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
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class StatsProcessor {
    private final Context context;

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
        List<UserApp> trackedUserApps = getTrackedUserApps();
        if (trackedUserApps.isEmpty()) {
            return;
        }

        AppListParseResults appListParseResults = parseAppList(trackedUserApps);
        TreeMap<String, UserApp> userAppMap = appListParseResults.getUserAppMap();

        Calendar nextDate = Calendar.getInstance();
        Date today = Date.from(Instant.now());
        for (Date curDate = appListParseResults.getMinDate(); curDate.before(today) || curDate.equals(today); ) {
            nextDate.setTime(curDate);
            nextDate.add(Calendar.DATE, 1);

            processDateStats(userAppMap, nextDate, curDate);

            curDate.setTime(nextDate.getTimeInMillis());
        }
    }

    private void processDateStats(TreeMap<String, UserApp> userAppMap, Calendar nextDate, Date curDate) {
        List<UsageStats> usageStats = queryUsageStats(curDate, nextDate);
        for (UsageStats usageStat : usageStats) {
            UserApp userApp = userAppMap.get(usageStat.getPackageName());
            if (userApp != null && userAppMap.containsKey(userApp.getPackageName())) {
                if (curDate.equals(userApp.getLastUpdateDate())) {
                    try {
                        DbHelperFactory.getHelper().getAppUseStatsDao().removeAppStatsPerDay(userApp, curDate);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (curDate.after(userApp.getLastUpdateDate()) || curDate.equals(userApp.getLastUpdateDate())) {
                    AppUseStats dbUseStats = createAppUseStatsForDate(curDate, usageStat, userApp);
                    try {
                        DbHelperFactory.getHelper().getAppUseStatsDao().create(dbUseStats);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<UsageStats> queryUsageStats(Date curDate, Calendar nextDate) {
        return ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE))
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, curDate.getTime(), nextDate.getTimeInMillis());
    }

    @NotNull
    private static AppUseStats createAppUseStatsForDate(Date curDate, UsageStats usageStat, UserApp userApp) {
        AppUseStats dbUseStats = new AppUseStats();
        dbUseStats.setDate(curDate);
        dbUseStats.setUsageTime(usageStat.getTotalTimeVisible());
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
        Date minDate = userApps.get(0).getLastUpdateDate();
        for (UserApp userApp : userApps) {
            userAppMap.put(userApp.getPackageName(), userApp);
            if (minDate.before(userApp.getLastUpdateDate())) {
                minDate = userApp.getLastUpdateDate();
            }
        }

        return new AppListParseResults(userAppMap, minDate);
    }

    public void addAppStats(UserApp userApp) {
        Date curDate = DateTimeUtils.getDateOfCurrentDayBegin();
        Calendar nextDate = Calendar.getInstance();
        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        while (curDate.before(today) || curDate.equals(today)) {
            nextDate.setTime(curDate);
            nextDate.add(Calendar.DATE, 1);

            for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
                if (userApp.getPackageName().equals(usageStat.getPackageName())) {
                    createAppUseStatsForDate(curDate, usageStat, userApp);
                    break;
                }
            }

            curDate.setTime(nextDate.getTimeInMillis());
        }
    }

    public void deleteAppStats(UserApp userApp) {
        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().removeAllAppStats(userApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
