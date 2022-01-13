package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UserAppDAO extends BaseDaoImpl<UserApp, Long> {
    protected UserAppDAO(ConnectionSource connectionSource, Class<UserApp> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<UserApp> getAllUserApps() throws SQLException {
        return this.queryForAll();
    }

    public List<UserApp> getAllTrackedApps() throws SQLException {
        QueryBuilder<UserApp, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(UserApp.FIELD_IS_TRACKED, true);
        PreparedQuery<UserApp> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

    public List<UserApp> getAllUntrackedApps() throws SQLException {
        QueryBuilder<UserApp, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(UserApp.FIELD_IS_TRACKED, false);
        PreparedQuery<UserApp> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

}
