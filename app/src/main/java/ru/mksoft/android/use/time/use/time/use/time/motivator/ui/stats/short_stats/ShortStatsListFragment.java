package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentShortStatsListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        RecyclerView recyclerView = binding.appListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShortStatsListRecyclerAdapter(this.getContext(), buildCategoryList()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<CategoryInShortSummary> buildCategoryList() {
        List<CategoryInShortSummary> shortSummaries = null;
        try {
            List<Category> categories = DbHelperFactory.getHelper().getCategoryDAO().getAllCategories();
            shortSummaries = new ArrayList<>(categories.size());
            for (Category category : categories) {
                shortSummaries.add(new CategoryInShortSummary(category, DbHelperFactory.getHelper().getAppUseStatsDao().getCategoryTodaySumStats(category)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shortSummaries;
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
