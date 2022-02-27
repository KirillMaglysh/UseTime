package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 07.01.2022
 */
public class AppListBuilder {
    private static final String LOG_TAG = AppListBuilder.class.getSimpleName();

    private final PackageManager packageManager;
    // TODO: Проверить необходимость хранинеия переменной
    private final Context context;

    /**
     * Constructor
     *
     * @param context application context
     */
    public AppListBuilder(Context context) {
        this.packageManager = context.getPackageManager();
        this.context = context;
    }

    /**
     * Updates information about application on the device in other thread
     */
    public void buildAppList() {
        new Thread(new Runnable() {
            private Category defaultCategory;

            @Override
            public void run() {
                try {
                    defaultCategory = DbHelperFactory.getHelper().getCategoryDAO().getDefaultCategory();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "Error getting default category");
                    throw new DatabaseException(e);
                }

                List<UserApp> untrackedApps = queryUntrackedApps();
                List<UserApp> trackedApps = queryTrackedApps();

                TreeMap<Integer, AppParams> sortedTracked = createAppsMap(trackedApps);
                TreeMap<Integer, AppParams> sortedUntracked = createAppsMap(untrackedApps);
                readCurrentAppList(sortedTracked, sortedUntracked);

                deleteNotFoundApps(trackedApps, sortedTracked);
                deleteNotFoundApps(untrackedApps, sortedUntracked);
            }

            private void readCurrentAppList(TreeMap<Integer, AppParams> sortedTracked, TreeMap<Integer, AppParams> sortedUntracked) {
                List<ApplicationInfo> notSortedAppList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo info : notSortedAppList) {
                    try {
                        if (sortedTracked.containsKey(info.uid)) {
                            sortedTracked.get(info.uid).wasFound = true;
                        } else if (sortedUntracked.containsKey(info.uid)) {
                            sortedUntracked.get(info.uid).wasFound = true;
                        } else if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                            addNewUntrackedAppIntoDB(info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private void addNewUntrackedAppIntoDB(ApplicationInfo info) throws SQLException {
                UserApp newApp = new UserApp();
                newApp.setSystemId(info.uid);
                newApp.setPackageName(String.valueOf(info.packageName));
                newApp.setCategory(defaultCategory);
                newApp.setLastUpdateDate(DateTimeUtils.getDateOfCurrentDayBegin());

                DbHelperFactory.getHelper().getUserAppDAO().create(newApp);
            }

            private TreeMap<Integer, AppParams> createAppsMap(List<UserApp> userApps) {
                TreeMap<Integer, AppParams> sortedTracked = new TreeMap<>();
                for (int i = 0; i < userApps.size(); i++) {
                    sortedTracked.put(userApps.get(i).getSystemId(), new AppParams(i));
                }

                return sortedTracked;
            }

            private void deleteNotFoundApps(List<UserApp> userApps, TreeMap<Integer, AppParams> sortedApps) {
                sortedApps.forEach((num, appParams) -> {
                    if (!appParams.wasFound) {
                        try {
                            DbHelperFactory.getHelper().getUserAppDAO().delete(userApps.get(appParams.originalPos));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public List<UserApp> queryUntrackedApps() {
        List<UserApp> untrackedApps = new ArrayList<>();
        try {
            untrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllUntrackedApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return untrackedApps;
    }

    public List<UserApp> queryTrackedApps() {
        List<UserApp> userApps = new ArrayList<>();
        try {
            userApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userApps;
    }

    enum AppListState {
        LOADED,
        LOADING,
        NOT_LOADED
    }

    static class AppParams {
        int originalPos;
        boolean wasFound;

        public AppParams(int originalPos) {
            this.originalPos = originalPos;
            wasFound = false;
        }
    }
}
