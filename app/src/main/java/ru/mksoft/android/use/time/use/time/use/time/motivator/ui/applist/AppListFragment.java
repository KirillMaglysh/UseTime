package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;

public class AppListFragment extends Fragment {
    private FragmentAppListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);

        PackageManager packageManager = getContext().getPackageManager();
        AppListBuilder appListBuilder = new AppListBuilder(packageManager);

        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AppListRecyclerAdapter(this, binding,
                appListBuilder.queryTrackedApps(), appListBuilder.queryUntrackedApps()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
