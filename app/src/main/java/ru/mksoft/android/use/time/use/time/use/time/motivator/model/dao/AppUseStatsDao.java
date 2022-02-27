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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class AppUseStatsDao extends BaseDaoImpl<AppUseStats, Long> {
    protected AppUseStatsDao(ConnectionSource connectionSource, Class<AppUseStats> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

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

    public int removeAppStatsPerDay(UserApp userApp, Date date) throws SQLException {
        DeleteBuilder<AppUseStats, Long> queryBuilder = deleteBuilder();
        deleteBuilder().where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .eq(AppUseStats.FIELD_DATE, date);

        PreparedDelete<AppUseStats> preparedQuery = queryBuilder.prepare();
        return delete(preparedQuery);
    }

    public AppUseStats getTodayAppStats(UserApp userApp) throws SQLException {
        QueryBuilder<AppUseStats, Long> queryBuilder = queryBuilder();
        Date date = new Date(45);
        LocalDate date1 = LocalDate.of(2002, 12, 16);
        date1.;

        queryBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .eq(AppUseStats.FIELD_DATE, Date.from(LocalDate.now().toString()));
        PreparedQuery<AppUseStats> preparedQuery = queryBuilder.prepare();

        return queryForFirst(preparedQuery);
    }

    public Long getCategoryTodaySumStats(Category category) throws SQLException {
        List<UserApp> userAppsForCategory = DbHelperFactory.getHelper().getUserAppDAO().getUserAppsForCategory(category);
        long sumUseTime = 0;
        for (UserApp userApp : userAppsForCategory) {
            sumUseTime += getTodayAppStats(userApp).getUsageTime();
        }

        return sumUseTime;
    }
}
