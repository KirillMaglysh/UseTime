package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.HashSet;
import java.util.Set;

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
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private final Set<RequestPackageUsageStatsPermissionListener> requestPackageUsageStatsPermissionListeners = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -10);
        long start = calendar.getTimeInMillis();
//        List<UsageStats> usageStats = ((UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE))
//                .queryUsageStats(UsageStatsManager.IN, start, end);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::processRequestUsageStatsPermissionResult
        );

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

    private void processRequestUsageStatsPermissionResult(ActivityResult result) {
        int mode = checkUsageStatsPermission();
        Log.i(LOG_TAG, "PACKAGE_USAGE_STATS permission changed to " + mode);

        for (RequestPackageUsageStatsPermissionListener requestPackageUsageStatsPermissionListener : requestPackageUsageStatsPermissionListeners) {
            requestPackageUsageStatsPermissionListener.onPermissionGranted(mode == AppOpsManager.MODE_ALLOWED);
        }
        requestPackageUsageStatsPermissionListeners.clear();
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
        int mode = checkUsageStatsPermission();

        if (mode == AppOpsManager.MODE_ALLOWED) {
            listener.onPermissionGranted(true);
            return;
        }

        Log.e(LOG_TAG, "Permission requested: " + requestPackageUsageStatsPermissionListeners.size());
        if (requestPackageUsageStatsPermissionListeners.isEmpty()) {
            // todo ограничить количество запросов и решить, что делать, если пользователь не даёт нужное разрешение
            someActivityResultLauncher.launch(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        requestPackageUsageStatsPermissionListeners.add(listener);
    }

    private int checkUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
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