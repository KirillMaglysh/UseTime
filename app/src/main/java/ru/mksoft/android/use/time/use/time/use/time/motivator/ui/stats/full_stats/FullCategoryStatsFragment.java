package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBinding(inflater, container);

        queryCategoryAndRule();
        visualizeCategoryAndRule();
        editChart();
        prepareChartDataSet();
        drawChart();

        return getRootView();
    }

    protected abstract void initBinding(@NotNull LayoutInflater inflater, ViewGroup container);

    protected abstract View getRootView();

    protected abstract void drawChart();

    protected abstract void prepareChartDataSet();

    private void queryCategoryAndRule() {
        category = queryCategory();
        rule = category.getRule();
    }

    protected abstract void visualizeCategoryAndRule();

    protected abstract void editChart();

    @Nullable
    protected abstract Category queryCategory();

    protected Category getCategory() {
        return category;
    }

    protected Rule getRule() {
        return rule;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
    }
}