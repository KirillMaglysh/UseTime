package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.TrackedApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UntrackedApp;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UntrackedAppDAO extends BaseDaoImpl<UntrackedApp, Long> {
    protected UntrackedAppDAO(ConnectionSource connectionSource, Class<UntrackedApp> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<UntrackedApp> getAllUntrackedApps() throws SQLException {
        return this.queryForAll();
    }
}
