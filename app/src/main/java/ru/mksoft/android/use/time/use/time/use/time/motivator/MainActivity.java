package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ActivityMainBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.CustomRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final PackageManager pm = this.getPackageManager();
//        List<PackageInfo> packs = pm.getInstalledPackages(0);
//
//        List<String> labels = new ArrayList<>();
//        for (int i = 0; i < packs.size(); i++) {
//            PackageInfo p = packs.get(i);
//            String description = (String) p.applicationInfo.loadDescription(pm);
//            String label = p.applicationInfo.loadLabel(pm).toString();
//            labels.add(label);
//            String packageName = p.packageName;
//            String versionName = p.versionName;
//            Drawable drawable = p.applicationInfo.loadIcon(pm);
//
//            Log.d("PDISK", "                      " + label + "=" + p.applicationInfo.);
////            if (!drawable.toString().contains("OplusAdaptiveIconDrawable")) {
////                Log.d("PICON", "                      " + label + "=" + drawable);
////            }
//        }
//
//        for (String label : labels) {
//            Log.d("LBL", label);
//        }




//        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//        List<ApplicationInfo> infoList = new ArrayList<>(packages);
//        for (ApplicationInfo applicationInfo : infoList) {
//            Drawable drawable = applicationInfo.loadIcon(pm);
////            if (!drawable.toString().contains("Oplus")) {
////                Log.d("AICON", "                      " + applicationInfo.loadLabel(pm));
////            }
////            if (!applicationInfo.toString().contains("com.android")) {
////                Log.d("QQQ", applicationInfo.toString());
////
////            }
//        }

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 1);
        beginCal.set(Calendar.MONTH, 0);
        beginCal.set(Calendar.YEAR, 2021);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DATE, 1);
        endCal.set(Calendar.MONTH, 0);
        endCal.set(Calendar.YEAR, 2022);
        final UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        System.out.println(queryUsageStats.size());
/*
        Category socialNet = new Category();
        socialNet.setName("socialNet");

        int i;
        try {
            i = DbHelperFactory.getHelper().getCategoryDAO().create(socialNet);
        } catch (SQLException e) {
            Log.e(TAG, "ошибка сохранения категории");
        }
*/

/*
        List<Category> allCategories;
        try {
            allCategories = DbHelperFactory.getHelper().getCategoryDAO().getAllCategories();
        } catch (SQLException e) {
            Log.e(TAG, "ошибка получения списка категорий");
        }
*/
/*
        Category socialNet = null;
        try {
            socialNet = DbHelperFactory.getHelper().getCategoryDAO().getCategoryByName("socialNet");
        } catch (SQLException e) {
            Log.e(TAG, "ошибка получения категории");
        }
*/

/*
        TrackedApp trackedApp = new TrackedApp();
        trackedApp.setCategory(socialNet);
        trackedApp.setName("VK.com");
        trackedApp.setSystemId("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        try {
            DbHelperFactory.getHelper().getTrackedAppDAO().create(trackedApp);
        } catch (SQLException e) {
            Log.e(TAG, "ошибка сохранения приложения");
        }
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}