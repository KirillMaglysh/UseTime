package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
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
import java.util.Date;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class AppUseStatsDAO extends BaseDaoImpl<AppUseStats, Long> {
    protected AppUseStatsDAO(ConnectionSource connectionSource, Class<AppUseStats> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Returns list of all categories which exist in database
     *
     * @return List of all categories which exist in database
     * @throws SQLException in case of incorrect work with database
     */
    public List<AppUseStats> getAllCategories() throws SQLException {
        return this.queryForAll();
    }

    @Override
    public AppUseStats queryForId(Long id) throws SQLException {
        return super.queryForId(id);
    }
/*

    public List<AppUseStats> getAppStatsForPeriod(UserApp userApp, SqlDateType start, SqlDateType end) throws SQLException {
        QueryBuilder<AppUseStats, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(AppUseStats.FIELD_USER_APP, userApp).between(AppUseStats.FIELD_DATE, start, end);
        PreparedQuery<AppUseStats> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }
*/

    /**
     * Removes AppUseStats stats for userApp for date.
     *
     * @param userApp application for which you want to remove stats
     * @param date    date, for which you want to remove stats
     * @return number of removed field in database
     * @throws SQLException in case of incorrect work with database
     */
    public int removeAppStatsPerDay(UserApp userApp, Date date) throws SQLException {
        DeleteBuilder<AppUseStats, Long> queryBuilder = deleteBuilder();
        deleteBuilder().where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .eq(AppUseStats.FIELD_DATE, date);

        PreparedDelete<AppUseStats> preparedQuery = queryBuilder.prepare();
        return delete(preparedQuery);
    }

    /**
     * Returns today AppUseStats of application
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
     * Returns summary today time of using category's applications
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
    }

    public int removeAllAppStats(UserApp userApp) throws SQLException {
        DeleteBuilder<AppUseStats, Long> queryBuilder = deleteBuilder();
        deleteBuilder().where()
                .eq(AppUseStats.FIELD_USER_APP, userApp);

        PreparedDelete<AppUseStats> preparedQuery = queryBuilder.prepare();
        return delete(preparedQuery);
    }
}
