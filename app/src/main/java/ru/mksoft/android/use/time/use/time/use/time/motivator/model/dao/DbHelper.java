package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "use_time_motivator.db";
    private static final int DATABASE_VERSION = 1;

    private CategoryDAO categoryDAO = null;
    private RuleDAO ruleDAO = null;
    private UserAppDAO userAppDAO = null;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, UserApp.class);
            TableUtils.createTable(connectionSource, Rule.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.w(TAG, "upgrade database");
    }

    public CategoryDAO getCategoryDAO() throws SQLException {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAO(getConnectionSource(), Category.class);
        }

        return categoryDAO;
    }

    public UserAppDAO getUserAppDAO() throws SQLException {
        if (userAppDAO == null) {
            userAppDAO = new UserAppDAO(getConnectionSource(), UserApp.class);
        }

        return userAppDAO;
    }

    public RuleDAO getRuleDAO() throws SQLException {
        if (ruleDAO == null) {
            ruleDAO = new RuleDAO(getConnectionSource(), Rule.class);
        }

        return ruleDAO;
    }
}
