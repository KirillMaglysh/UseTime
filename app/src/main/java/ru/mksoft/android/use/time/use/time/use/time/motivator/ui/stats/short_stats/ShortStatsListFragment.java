package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentShortStatsListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShortStatsListFragment#} factory method to
 * create an instance of this fragment.
 */
public class ShortStatsListFragment extends Fragment {
    private FragmentShortStatsListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShortStatsListBinding.inflate(inflater, container, false);

        PackageManager packageManager = getContext().getPackageManager();

        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShortStatsListRecyclerAdapter(this, binding, appListBuilder.queryTrackedApps()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class CategoryInShortSummary {
        private Category category;
        String[] time;

        public CategoryInShortSummary(Category category, long timeInMilliseconds) {
            this.category = category;
            time = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SSS", Locale.US).format(timeInMilliseconds).split(" ");
        }
    }
}
