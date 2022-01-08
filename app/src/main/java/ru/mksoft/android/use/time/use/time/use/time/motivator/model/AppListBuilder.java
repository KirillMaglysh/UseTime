package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

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
    private PackageManager packageManager;

    public AppListBuilder(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public void buildAppList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<UntrackedApp> allUntrackedApps = queryUntrackedApps();
                List<TrackedApp> allTrackedApps = queryTrackedApps();

                TreeMap<Integer, AppParams> sortedTracked = createTrackedAppsMap(allTrackedApps);
                TreeMap<Integer, AppParams> sortedUntracked = createUntrackedAppsMap(allUntrackedApps);
                addNewUntrackedApps(sortedTracked, sortedUntracked);

                deleteNotFoundTrackedApps(allTrackedApps, sortedTracked);
                deleteNotFoundUntrackedApps(allUntrackedApps, sortedUntracked);
            }

            private void addNewUntrackedApps(TreeMap<Integer, AppParams> sortedTracked, TreeMap<Integer, AppParams> sortedUntracked) {
                List<ApplicationInfo> notSortedAppList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo info : notSortedAppList) {
                    try {
                        if (sortedTracked.containsKey(info.uid)) {
                            sortedTracked.get(info.uid).wasFound = true;
                        } else if (sortedUntracked.containsKey(info.uid)) {
                            sortedUntracked.get(info.uid).wasFound = true;
                        } else if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                            UntrackedApp newApp = new UntrackedApp();
                            newApp.setSystemId(info.uid);
                            newApp.setName(String.valueOf(info.loadLabel(packageManager)));

                            DbHelperFactory.getHelper().getUntrackedAppDAO().create(newApp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private TreeMap<Integer, AppParams> createTrackedAppsMap(List<TrackedApp> allTrackedApps) {
                TreeMap<Integer, AppParams> sortedTracked = new TreeMap<>();
                for (int i = 0; i < allTrackedApps.size(); i++) {
                    sortedTracked.put(allTrackedApps.get(i).getSystemId(), new AppParams(i));
                }

                return sortedTracked;
            }

            private TreeMap<Integer, AppParams> createUntrackedAppsMap(List<UntrackedApp> allUntrackedApps) {
                TreeMap<Integer, AppParams> sortedUntracked = new TreeMap<>();
                for (int i = 0; i < allUntrackedApps.size(); i++) {
                    sortedUntracked.put(allUntrackedApps.get(i).getSystemId(), new AppParams(i));
                }

                return sortedUntracked;
            }


            private void deleteNotFoundTrackedApps(List<TrackedApp> allTrackedApps, TreeMap<Integer, AppParams> sortedTracked) {
                sortedTracked.forEach((num, appParams) -> {
                    if (!appParams.wasFound) {
                        try {
                            DbHelperFactory.getHelper().getTrackedAppDAO().delete(allTrackedApps.get(appParams.originalPos));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            private void deleteNotFoundUntrackedApps(List<UntrackedApp> allUntrackedApps, TreeMap<Integer, AppParams> sortedUntracked) {
                sortedUntracked.forEach((integer, appParams) -> {
                    if (!appParams.wasFound) {
                        try {
                            DbHelperFactory.getHelper().getUntrackedAppDAO().delete(allUntrackedApps.get(appParams.originalPos));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public List<UntrackedApp> queryUntrackedApps() {
        List<UntrackedApp> untrackedApps = new ArrayList<>();
        try {
            untrackedApps = DbHelperFactory.getHelper().getUntrackedAppDAO().getAllUntrackedApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return untrackedApps;
    }

    public List<TrackedApp> queryTrackedApps() {
        List<TrackedApp> trackedApps = new ArrayList<>();
        try {
            trackedApps = DbHelperFactory.getHelper().getTrackedAppDAO().getAllTrackedApps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trackedApps;
    }

    enum AppListState {
        LOADED,
        LOADING,
        NOT_LOADED
    }

    class AppParams {
        int originalPos;
        boolean wasFound;

        public AppParams(int originalPos) {
            this.originalPos = originalPos;
            wasFound = false;
        }
    }
}
