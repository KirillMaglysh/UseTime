package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 11.02.2022
 */
public class RuleDAO extends BaseDaoImpl<Rule, Long> {
    protected RuleDAO(ConnectionSource connectionSource, Class<Rule> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Rule> getAllRules() throws SQLException {
        return this.queryForAll();
    }

    public Rule getRuleByName(String name) throws SQLException {
        QueryBuilder<Rule, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Rule.FIELD_RULE_NAME, name);
        PreparedQuery<Rule> preparedQuery = queryBuilder.prepare();

        return queryForFirst(preparedQuery);
    }

    @Override
    public Rule queryForId(Long id) throws SQLException {
        return super.queryForId(id);
    }
}
