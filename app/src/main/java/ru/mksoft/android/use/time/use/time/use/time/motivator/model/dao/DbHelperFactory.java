package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class DbHelperFactory {
    private static DbHelper dbHelper;

    public static DbHelper getHelper() {
        return dbHelper;
    }

    public static void setHelper(Context context) {
        dbHelper = OpenHelperManager.getHelper(context, DbHelper.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        dbHelper = null;
    }
}
