package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ActivityMainBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;

import java.util.Calendar;

/**
 * Main and single activity of the app
 *
 * @author Kirill
 * @since 18.11.21
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private AppListBuilder appListBuilder;
    private StatsProcessor statsProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -10);
        long start = calendar.getTimeInMillis();
//        List<UsageStats> usageStats = ((UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE))
//                .queryUsageStats(UsageStatsManager.IN, start, end);

        appListBuilder = new AppListBuilder(this);
        statsProcessor = new StatsProcessor(this);
        appListBuilder.buildAppList();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        buildTopLevelMenu(drawer);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void buildTopLevelMenu(DrawerLayout drawer) {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_applist,
                R.id.nav_category_list,
                R.id.nav_rule_list,
                R.id.nav_short_stats_list
        ).setOpenableLayout(drawer).build();
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

    @Override
    protected void onResume() {
        appListBuilder.buildAppList();
        super.onResume();
    }

    public AppListBuilder getAppListBuilder() {
        return appListBuilder;
    }

    /**
     * Return statistics processor.
     *
     * @return statistics processor
     */
    public StatsProcessor getStatsProcessor() {
        return statsProcessor;
    }

    /**
     * Request PACKAGE_USAGE_STATS permission.
     *
     * @param listener request listener
     */
    public void requestPackageUsageStatsPermission(RequestPackageUsageStatsPermissionListener listener) {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = checkUsageStatsPermission
                (appOps);

        if (mode == AppOpsManager.MODE_ALLOWED) {
            listener.onPermissionGranted(true);
            return;
        }

        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getPackageName(),
                (operation, packageName) -> {
                    int newMode = checkUsageStatsPermission(appOps);
                    Log.i(LOG_TAG, "PACKAGE_USAGE_STATS permission changed to " + newMode);
                    listener.onPermissionGranted(newMode == AppOpsManager.MODE_ALLOWED);
                });

        // todo ограничить количество запросов и решить, что делать, если пользователь не даёт нужное разрешение
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private int checkUsageStatsPermission(AppOpsManager appOps) {
        return appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
    }

    /**
     * Listener for request statistics processor result.
     */
    public interface RequestPackageUsageStatsPermissionListener {
        /**
         * Action when permission requested
         *
         * @param isGranted true, if granted
         */
        void onPermissionGranted(boolean isGranted);
    }
}