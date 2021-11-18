package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.TrackedApp;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class TrackedAppDAO extends BaseDaoImpl<TrackedApp, Long> {
    protected TrackedAppDAO(ConnectionSource connectionSource, Class<TrackedApp> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<TrackedApp> getAllApps() throws SQLException {
        return this.queryForAll();
    }
}
