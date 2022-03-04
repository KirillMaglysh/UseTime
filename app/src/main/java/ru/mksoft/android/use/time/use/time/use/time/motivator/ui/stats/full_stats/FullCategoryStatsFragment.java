package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentFullCategoryStatsBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullCategoryStatsFragment} factory method to
 * create an instance of this fragment.
 *
 * @author Kirill
 * @since 01.03.2022
 */
public class FullCategoryStatsFragment extends Fragment {
    private BarChart barChart;
    private FragmentFullCategoryStatsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFullCategoryStatsBinding.inflate(inflater, container, false);
        barChart = binding.categoryStats7DayBarChart;

        Category category = getCategory();
        binding.categoryInFullStatsLabel.setText(category.getName());

        List<Integer> stats = getStats(category);
        createDataEntries(stats);

        List<BarEntry> entries = createDataEntries(stats);

        BarDataSet dataset = new BarDataSet(entries, "");
        dataset.setValueFormatter(new BarItemFormatter());

        barChart.getLegend().setEnabled(false);
//        Description description = new Description();
//        description.setText("Использование за последние 7 дней");
//        barChart.setDescription(description);
        barChart.getDescription().setEnabled(false);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        setXAxis();
        setYAxis();

        barChart.setData(new BarData(dataset, dataset));
        barChart.animateY(3000);

        return binding.getRoot();
    }

    private static List<BarEntry> createDataEntries(List<Integer> stats) {
        List<BarEntry> entries = new ArrayList<>();
        float xPos = 1f;
        for (Integer stat : stats) {
            entries.add(new BarEntry(xPos++, stat));
        }

        return entries;
    }

    @Nullable
    private Category getCategory() {
        FullCategoryStatsFragmentArgs fragmentArgs = FullCategoryStatsFragmentArgs.fromBundle(getArguments());
        Category category = null;
        try {
            category = DbHelperFactory.getHelper().getCategoryDAO().queryForId(Long.valueOf(fragmentArgs.getCategoryId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    private static List<Integer> getStats(Category category) {
        List<Integer> stats = new ArrayList<>();
        try {
            List<Long> longStats = DbHelperFactory.getHelper().getAppUseStatsDao().getCategorySumSuffixTimeStats(category, 7);
            for (Long longStat : longStats) {
                stats.add((int) (longStat / 60000));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    private void setXAxis() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(7);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new XAxisValueFormatter());
    }

    private void setYAxis() {
        barChart.getAxisRight().setEnabled(false);
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setValueFormatter(new LeftAxisValueFormatter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
    }

    private static class LeftAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return DateTimeUtils.getFormattedMinutesTime((int) value);
        }
    }

    private static class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return DateTimeUtils.DAY_LABELS_RU[(int) value - 1];
        }
    }

    private static class BarItemFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return DateTimeUtils.getFormattedMinutesTime((int) value);
        }
    }
}