package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppUseStats;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Application usage statistics data access object.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class AppUseStatsDAO extends BaseDaoImpl<AppUseStats, Long> {
    protected AppUseStatsDAO(ConnectionSource connectionSource, Class<AppUseStats> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
//TODO() переделать методы с циклами на конкретные запросы к базе

    /**
     * Returns list of all categories which exist in database.
     *
     * @return List of all categories which exist in database
     * @throws SQLException in case of incorrect work with database
     */
    public List<AppUseStats> getAllStats() throws SQLException {
        return this.queryForAll();
    }

    @Override
    public AppUseStats queryForId(Long id) throws SQLException {
        return super.queryForId(id);
    }

    public List<AppUseStats> getAppStatsForPeriod(UserApp userApp, Date start, Date end) throws SQLException {
        QueryBuilder<AppUseStats, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .between(AppUseStats.FIELD_DATE, start, end);
        PreparedQuery<AppUseStats> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

    /**
     * Returns sum stats for every day for this category during the period (today - dayNum, today]
     *
     * @param category category stats for which you want to get
     * @param dayNum   number of day for which you want to get stats
     * @return sum stats for every day for this category during the period (today - dayNum, today]
     * @throws SQLException in case of incorrect work with database
     */
    public List<Long> getCategorySumSuffixTimeStats(Category category, int dayNum) throws SQLException {
        List<UserApp> userApps = DbHelperFactory.getHelper().getUserAppDAO().getTrackedUserAppsForCategory(category);
        List<Long> timeByDays = new ArrayList<>(dayNum);
        for (int i = 0; i < dayNum; i++) {
            timeByDays.add(0L);
        }

        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        Date startDay = DateTimeUtils.getDateOtherDayBegin(-dayNum + 1);
        for (UserApp userApp : userApps) {
            List<AppUseStats> statsForPeriod = getAppStatsForPeriod(userApp, startDay, today);
            for (int i = 0; i < timeByDays.size(); i++) {
                timeByDays.set(i, timeByDays.get(i) + statsForPeriod.get(i).getUsageTime());
            }
        }

        return timeByDays;
    }

    /**
     * Removes AppUseStats stats for userApp for date.
     *
     * @param userApp application for which you want to remove stats
     * @param date    date, for which you want to remove stats
     * @return number of removed field in database
     * @throws SQLException in case of incorrect work with database
     */
    public int removeAppStatsPerDay(UserApp userApp, Date date) throws SQLException {
        DeleteBuilder<AppUseStats, Long> deleteBuilder = deleteBuilder();
        deleteBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .eq(AppUseStats.FIELD_DATE, date);

        PreparedDelete<AppUseStats> preparedQuery = deleteBuilder.prepare();
        return delete(preparedQuery);
    }

    /**
     * Returns summary today time of using category's applications.
     *
     * @param category category, for which you want to get stats
     * @return summary time today of using category's applications
     * @throws SQLException in case of incorrect work with database
     */
    public Long getCategoryTodaySumStats(Category category) throws SQLException {
        List<UserApp> userAppsForCategory = DbHelperFactory.getHelper().getUserAppDAO().getTrackedUserAppsForCategory(category);
        long sumUseTime = 0;
        for (UserApp userApp : userAppsForCategory) {
            sumUseTime += getTodayAppStats(userApp).getUsageTime();
        }

        return sumUseTime;

/*
        GenericRawResults<String[]> results = this.queryRaw("select sum(USAGE_TIME)" +
                "from APP_USAGE_STATS" +
                "where USER_APP in {" +
                "select id" +
                "from USER_APP" +
                "where CATEGORY = ???" +
                "}" +
                "and DATE  = ???" +
                "GROUP BY DATE");
*/
    }

    /**
     * Returns today AppUseStats of application.
     *
     * @param userApp application, for which you want to get stats
     * @return Returns today AppUseStats of application
     * @throws SQLException in case of incorrect work with database
     */
    public AppUseStats getTodayAppStats(UserApp userApp) throws SQLException {
        QueryBuilder<AppUseStats, Long> queryBuilder = queryBuilder();

        queryBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .eq(AppUseStats.FIELD_DATE, DateTimeUtils.getDateOfCurrentDayBegin());
        PreparedQuery<AppUseStats> preparedQuery = queryBuilder.prepare();

        return queryForFirst(preparedQuery);
    }



    /**
     * Clears application usage statistics.
     *
     * @param userApp application
     * @throws SQLException in case of incorrect work with database
     */
    public void removeAppAllStats(UserApp userApp) throws SQLException {
        DeleteBuilder<AppUseStats, Long> queryBuilder = deleteBuilder();
        deleteBuilder().where()
                .eq(AppUseStats.FIELD_USER_APP, userApp);

        PreparedDelete<AppUseStats> preparedQuery = queryBuilder.prepare();
        delete(preparedQuery);
    }
}
