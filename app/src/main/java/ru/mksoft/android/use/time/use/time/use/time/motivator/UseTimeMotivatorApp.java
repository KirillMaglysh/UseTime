package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.Application;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UseTimeMotivatorApp extends Application {
    @Override
    public void onCreate() {
//        deleteDatabase("use_time_motivator.db");
        super.onCreate();
        DbHelperFactory.setHelper(getApplicationContext());
        new AppListBuilder(getApplicationContext()).buildAppList();
    }

    @Override
    public void onTerminate() {
        DbHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
