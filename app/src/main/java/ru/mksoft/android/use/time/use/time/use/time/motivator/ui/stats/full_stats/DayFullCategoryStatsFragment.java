package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentDayFullCategoryStatsBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.05.2022
 */
public class DayFullCategoryStatsFragment extends FullCategoryStatsFragment {
    private PieChart pieChart;
    private FragmentDayFullCategoryStatsBinding binding;

    @Override
    protected void initBinding(@NotNull LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDayFullCategoryStatsBinding.inflate(inflater, container, false);
    }

    @Override
    protected View getRootView() {
        return binding.getRoot();
    }

    @Override
    protected void drawChart() {
        pieChart.animateXY(1000, 1000);
    }

    @Override
    protected void prepareChartDataSet() {

    }

    @Override
    protected void visualizeCategoryAndRule() {

    }

    @Override
    protected void editChart() {

    }

    @Nullable
    @Override
    protected Category queryCategory() {
        DayFullCategoryStatsFragmentArgs fragmentArgs = DayFullCategoryStatsFragmentArgs.fromBundle(getArguments());
        Category category = null;
        try {
            category = DbHelperFactory.getHelper().getCategoryDAO().queryForId(Long.valueOf(fragmentArgs.getCategoryId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }
}
