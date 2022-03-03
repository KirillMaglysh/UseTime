package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelper.PREDEFINED_ID;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class CategoryDAO extends BaseDaoImpl<Category, Long> {
    protected CategoryDAO(ConnectionSource connectionSource, Class<Category> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Returns a list of all categories.
     *
     * @return list of all categories
     * @throws SQLException database error
     */
    public List<Category> getAllCategories() throws SQLException {
        return this.queryForAll();
    }

    /**
     * Returns a list of all categories, excluding the default one
     *
     * @return list of all categories
     * @throws SQLException database error
     */
    public List<Category> getAllCategoriesWoDefault() throws SQLException {
        QueryBuilder<Category, Long> queryBuilder = queryBuilder();
        queryBuilder.where().ne("id", PREDEFINED_ID);
        return query(queryBuilder.prepare());
    }

    /**
     * Returns the number of all categories, excluding the default one
     *
     * @return number of all categories
     * @throws SQLException database error
     */
    public long getCategoriesWoDefaultCount() throws SQLException {
        return queryBuilder().where().ne("id", PREDEFINED_ID).countOf();
    }

    public Category getCategoryByName(String name) throws SQLException {
        QueryBuilder<Category, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Category.FIELD_CATEGORY_NAME, name);
        PreparedQuery<Category> preparedQuery = queryBuilder.prepare();

        return queryForFirst(preparedQuery);
    }

    @Override
    public Category queryForId(Long id) throws SQLException {
        return super.queryForId(id);
    }

    public Category getDefaultCategory() throws SQLException {
        return queryForId(PREDEFINED_ID);
    }
}
