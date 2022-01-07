package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppListFragment extends Fragment {

    private AppListViewModel applistViewModel;
    private FragmentAppListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        applistViewModel =
                new ViewModelProvider(this).get(AppListViewModel.class);

        binding = FragmentAppListBinding.inflate(inflater, container, false);

        long startReading = Calendar.getInstance().getTimeInMillis();
        PackageManager packageManager = getContext().getPackageManager();
        ArrayList<ApplicationInfo> appList = new ArrayList<>();
        List<ApplicationInfo> notSortedAppList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        int cycleNum = 0;
        for (ApplicationInfo info : notSortedAppList) {
            cycleNum++;
            try {
                if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("CycleNum", String.valueOf(cycleNum));

        long endReading = Calendar.getInstance().getTimeInMillis();

        List<AppCardInfo> appCards = new ArrayList<>(appList.size());
        //Todo передать appInfo
        for (ApplicationInfo applicationInfo : appList) {
            appCards.add(new AppCardInfo((String) applicationInfo.loadLabel(packageManager), applicationInfo.loadIcon(packageManager), "ABCABA"));
        }


        long startWriting = Calendar.getInstance().getTimeInMillis();
        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomRecyclerAdapter(appCards));
        long endWriting = Calendar.getInstance().getTimeInMillis();

        Log.d("ReadTime", String.valueOf(endReading - startReading));
        Log.d("WriteTime", String.valueOf(endWriting - startWriting));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}