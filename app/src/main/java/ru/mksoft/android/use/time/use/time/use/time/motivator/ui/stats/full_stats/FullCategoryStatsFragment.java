package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.graphics.Color;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentFullCategoryStatsBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.RuleViewHolder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static final int LABEL_NUM = Calendar.DAY_OF_WEEK;
    private BarChart barChart;
    private final String[] xAxisLabels = new String[LABEL_NUM];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentFullCategoryStatsBinding binding = FragmentFullCategoryStatsBinding.inflate(inflater, container, false);
        barChart = binding.categoryStats7DayBarChart;
        editBarChart();
        Category category = getCategory();
        Rule rule = category.getRule();

        new RuleViewHolder(binding.ruleBodyInFullStats.getRoot()).fillRuleData(rule);

        binding.categoryInFullStatsLabel.setText(category.getName());

        List<Integer> stats = getStats(category);

        BarDataSet dataset = new BarDataSet(createDataEntries(stats), "");
        dataset.setValueFormatter(new BarItemFormatter());
        dataset.setColors(createColorSet(stats, rule));

        barChart.setData(new BarData(dataset, dataset));
        barChart.animateY(1000);

        return binding.getRoot();
    }

    private void editBarChart() {
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        setXAxis();
        setYAxis();
    }

    private static List<BarEntry> createDataEntries(List<Integer> stats) {
        List<BarEntry> entries = new ArrayList<>();
        float xPos = 1f;
        for (Integer stat : stats) {
            entries.add(new BarEntry(xPos++, stat));
        }

        return entries;
    }

    private static int[] createColorSet(List<Integer> stats, Rule rule) {
        int[] colorSet = new int[LABEL_NUM];
        Calendar calendar = Calendar.getInstance();
        for (int i = LABEL_NUM - 1; i >= 0; i--) {
            Integer timeLimit = rule.getTime(Rule.DayOfWeek.values()[DateTimeUtils.getDayOfWeek(calendar)]);

            // TODO попробовать упростить логику определения цвета
            // TODO подумать над количеством градаций уровня
            int factor = Math.min((int) (stats.get(i) / (timeLimit / 100f)), 255);
            if (timeLimit >= stats.get(i)) {
                colorSet[i] = Color.rgb(factor, 255, 0);
            } else {
                colorSet[i] = Color.rgb(255, 255 - factor, 0);
            }

            calendar.add(Calendar.DATE, -1);
        }

        return colorSet;
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
            List<Long> longStats = DbHelperFactory.getHelper().getAppUseStatsDao().getCategorySumSuffixTimeStats(category, LABEL_NUM);
            for (Long longStat : longStats) {
                stats.add((int) (longStat / DateTimeUtils.MILLIS_IN_MINUTE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    private void setXAxis() {
        Calendar calendar = Calendar.getInstance();
        for (int i = LABEL_NUM - 1; i >= 0; i--) {
            xAxisLabels[i] = DateTimeUtils.getFormattedDateWithDayOfWeek(calendar);
            calendar.add(Calendar.DATE, -1);
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(8);
        xAxis.setLabelCount(LABEL_NUM);
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

    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return xAxisLabels[(int) value - 1];
        }
    }

    private static class BarItemFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return DateTimeUtils.getFormattedMinutesTime((int) value);
        }
    }
}