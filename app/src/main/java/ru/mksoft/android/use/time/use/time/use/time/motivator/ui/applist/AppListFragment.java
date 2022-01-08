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
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppListFragment extends Fragment {

    private AppListViewModel applistViewModel;
    private FragmentAppListBinding binding;
    private AppListBuilder appListBuilder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        applistViewModel =
                new ViewModelProvider(this).get(AppListViewModel.class);

        binding = FragmentAppListBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appListBuilder = new AppListBuilder(getContext().getPackageManager());
        recyclerView.setAdapter(new CustomRecyclerAdapter(appListBuilder.queryTrackedApps(), appListBuilder.queryUntrackedApps()));


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}