package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Fragment which shows all installed user applications. Here user can track or untrack applications
 *
 * @author Kirill
 * @since 15.01.2022
 */
public class AppListFragment extends Fragment implements AppListBuilder.AppListBuiltListener {
    private FragmentAppListBinding binding;
    private ConstraintLayout progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        AppListBuilder appListBuilder = ((MainActivity) getContext()).getAppListBuilder();
        progressBar = binding.appListProgressWindow.progressWindow;
        if (appListBuilder.isBuilt()) {
            processAppListBuilt();
        } else {
            appListBuilder.subscribe(this);
            progressBar.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getContext()).getAppListBuilder().unsubscribeUIListener();
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void processAppListBuilt() {
        ((MainActivity) getContext()).getAppListBuilder().unsubscribeUIListener();
        ((MainActivity) getContext()).runOnUiThread(() -> {
            RecyclerView recyclerView = binding.appListRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            List<UserApp> allTrackedApps = null;
            List<UserApp> allUntrackedApps = null;
            try {
                allTrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
                allUntrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllUntrackedApps();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            recyclerView.setAdapter(new AppListRecyclerAdapter(this, binding, allTrackedApps, allUntrackedApps));
            progressBar.setVisibility(View.INVISIBLE);
        });
    }
}
