package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;

import java.util.ArrayList;
import java.util.List;

public class AppListFragment extends Fragment {

    private AppListViewModel applistViewModel;
    private FragmentAppListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        applistViewModel =
                new ViewModelProvider(this).get(AppListViewModel.class);

        binding = FragmentAppListBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

        PackageManager pm = getContext().getPackageManager();
        ArrayList<ApplicationInfo> appList = new ArrayList<>();
        for (ApplicationInfo info : pm.getInstalledApplications(PackageManager.GET_META_DATA)) {
            try {
                if (pm.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> appLabels = new ArrayList<>(appList.size());
        for (ApplicationInfo applicationInfo : appList) {
            appLabels.add((String) applicationInfo.loadLabel(pm));
        }


        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomRecyclerAdapter(appLabels));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}