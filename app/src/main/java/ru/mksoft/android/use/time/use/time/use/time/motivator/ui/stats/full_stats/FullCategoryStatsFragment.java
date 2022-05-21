package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;

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
public abstract class FullCategoryStatsFragment extends Fragment {
    private Category category;
    private Rule rule;
    private PieChart pieChart;
    private List<AppStatsBin> appStatsBins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBinding(inflater, container);

        queryCategoryAndRule();
        appStatsBins = queryAppStats();
        drawOwnPart();
        initAppPieChart();
        drawAppChart();
        drawCategoryAppStatsList();

        return getRootView();
    }

    protected abstract void initAppPieChart();

    protected abstract void drawOwnPart();

    private void drawAppChart() {
        editAppChart();
        fillAppChartDataSet();
        pieChart.animateXY(1000, 1000);
    }

    private void fillAppChartDataSet() {
        PieDataSet dataset = new PieDataSet(createDataEntries(), "");
        dataset.setValueFormatter(new PieItemFormatter());

        pieChart.setData(new PieData(dataset));
    }

    private List<PieEntry> createDataEntries() {
        PackageManager pm = getContext().getPackageManager();
        List<PieEntry> entries = new ArrayList<>();
        for (AppStatsBin appStatsBin : appStatsBins) {
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(appStatsBin.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }

            entries.add(new PieEntry(appStatsBin.getUsedTime(), pm.getApplicationLabel(applicationInfo)));
        }

        return entries;
    }

    private void editAppChart() {
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Apps");
    }

    protected abstract void initBinding(@NotNull LayoutInflater inflater, ViewGroup container);

    protected abstract View getRootView();

    private void queryCategoryAndRule() {
        category = queryCategory();
        rule = category.getRule();
    }

    @Nullable
    protected abstract Category queryCategory();

    protected abstract List<AppStatsBin> queryAppStats();

    private void drawCategoryAppStatsList() {
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AppListStatsRecyclerAdapter(getContext(), appStatsBins));
    }

    protected abstract RecyclerView getRecyclerView();

    protected Category getCategory() {
        return category;
    }

    protected Rule getRule() {
        return rule;
    }

    protected void setAppPieChart(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
    }

    private static class PieItemFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "";
        }
    }
}