package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldConverter;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.types.DateStringType;
import com.j256.ormlite.field.types.DateTimeType;
import com.j256.ormlite.field.types.SqlDateStringType;
import com.j256.ormlite.field.types.TimeStampType;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
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

    public StatsProcessor(Context context) {
        this.context = context;
    }

    public void updateUseStats() {
        List<UserApp> userApps = null;
        try {
            userApps = DbHelperFactory.getHelper().getUserAppDAO().getAllUserApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TreeMap<String, UserApp> userAppMap = new TreeMap<>();
        for (UserApp userApp : userApps) {
            userAppMap.put(userApp.getPackageName(), userApp);
        }

        long startTime = 0;
        long endTime = 0;
        for (int i = 1; i < Calendar.DAY_OF_WEEK; i++) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

            for (UsageStats usageStat : usageStats) {
                UserApp userApp = userAppMap.get(usageStat.getPackageName());


                AppUseStats appUseStats = new AppUseStats();
                DateStringType.getSingleton().
                TimeStampType.getSingleton().sqlArgToJava(,appUseStats.getDate(), )

//                if (userApp != null && ) {
//                    SqlDateStringType.getSingleton().sqlArgToJava(DataType.SQL_DATE, userApp.getLastUpdate(), 0);
//                    FieldConverter
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();

        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
//        long startTime = instant.toEpochMilli();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }
}
