package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.02.2022
 */
public class Storage {
    private static volatile Storage instance;
    private AppListRecyclerAdapter appListRecyclerAdapter;

    public static Storage getInstance() {
        Storage localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Storage();
                }
            }
        }
        return localInstance;
    }

    public AppListRecyclerAdapter getAppListRecyclerAdapter() {
        return appListRecyclerAdapter;
    }

    public void setAppListRecyclerAdapter(AppListRecyclerAdapter appListRecyclerAdapter) {
        this.appListRecyclerAdapter = appListRecyclerAdapter;
    }
}
