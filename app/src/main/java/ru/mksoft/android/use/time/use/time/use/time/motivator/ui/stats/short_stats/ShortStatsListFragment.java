package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentShortStatsListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShortStatsListFragment#} factory method to
 * create an instance of this fragment.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class ShortStatsListFragment extends Fragment implements StatsProcessor.StatsProcessedUIListener {
    private FragmentShortStatsListBinding binding;
    private ConstraintLayout progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShortStatsListBinding.inflate(inflater, container, false);

        StatsProcessor statsProcessor = ((MainActivity) getContext()).getStatsProcessor();
        progressBar = binding.shortStatsListProgressWindow.progressWindow;
        if (statsProcessor.isProcessed()) {
            processStatsProcessedBuilt();
        } else {
            statsProcessor.subscribe(this);
            progressBar.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        binding = null;
    }

    private List<CategoryInShortSummary> buildCategoryList() {
        List<CategoryInShortSummary> shortSummaries = null;
        try {
            List<Category> categories = DbHelperFactory.getHelper().getCategoryDAO().getAllCategoriesWoDefault();
            shortSummaries = new ArrayList<>(categories.size());
            for (Category category : categories) {
                shortSummaries.add(new CategoryInShortSummary(category, DbHelperFactory.getHelper().getAppUseStatsDao().getCategoryTodaySumStats(category)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shortSummaries;
    }

    @Override
    public void processStatsProcessedBuilt() {
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        ((MainActivity) getContext()).runOnUiThread(() -> {
            RecyclerView recyclerView = binding.statsListRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new ShortStatsListRecyclerAdapter(this.getContext(), buildCategoryList()));
        });

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Getter
    public class CategoryInShortSummary {
        private Category category;
        int minutes;
        int hours;
//        private String[] time;

        public CategoryInShortSummary(Category category, long timeInMilliseconds) {
            this.category = category;
            minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) % 60;
            hours = (int) TimeUnit.MILLISECONDS.toHours(timeInMilliseconds) % 24;
//            new DateFormat("HH mm", Locale.getDefault()).format(timeInMilliseconds).split(" ");
//            time = new SimpleDateFormat("HH mm", Locale.UK).format(timeInMilliseconds).split(" ");
        }
    }
}
