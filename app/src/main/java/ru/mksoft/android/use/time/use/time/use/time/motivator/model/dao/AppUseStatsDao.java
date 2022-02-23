package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppUseStats;

import java.sql.SQLException;
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
}
