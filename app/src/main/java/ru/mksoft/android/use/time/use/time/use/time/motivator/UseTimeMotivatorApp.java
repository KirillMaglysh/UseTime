package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.Application;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UseTimeMotivatorApp extends Application {
    @Override
    public void onCreate() {
        deleteDatabase("use_time_motivator.db");
        super.onCreate();
        DbHelperFactory.setHelper(getApplicationContext());
        try {
            DbHelperFactory.getHelper().getCategoryDAO().getDefaultCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new AppListBuilder(getPackageManager()).buildAppList();
    }

    @Override
    public void onTerminate() {
        DbHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
