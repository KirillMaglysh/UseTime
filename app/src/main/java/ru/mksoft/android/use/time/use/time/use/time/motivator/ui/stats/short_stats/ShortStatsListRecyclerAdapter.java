package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListFragmentDirections;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class ShortStatsListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShortStatsListFragment.CategoryInShortSummary> categoriesInShortSummary;
    private Context context;

    public ShortStatsListRecyclerAdapter(Context context, List<ShortStatsListFragment.CategoryInShortSummary> categoriesInShortSummary) {
        this.categoriesInShortSummary = categoriesInShortSummary;
        this.context = context;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CategoryIntShortStatsCardHolder(LayoutInflater.from(context).inflate(R.layout.category_short_stats_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        CategoryIntShortStatsCardHolder cardHolder = (CategoryIntShortStatsCardHolder) holder;
        ShortStatsListFragment.CategoryInShortSummary categoryStats = categoriesInShortSummary.get(position);
        cardHolder.hourValInShortCategoryStats.setText(categoryStats.getTime()[0]);
        cardHolder.minValInShortCategoryStats.setText(categoryStats.getTime()[1]);
        cardHolder.categoryInShortStatsLabel.setText(categoryStats.getCategory().getName());

        ((CategoryIntShortStatsCardHolder) holder).moreButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                .navigate(ShortStatsListFragmentDirections.actionNavShortStatsListToNavFullStats(categoryStats.getCategory().getId().toString())));
    }

    @Override
    public int getItemCount() {
        return categoriesInShortSummary.size();
    }

    class CategoryIntShortStatsCardHolder extends RecyclerView.ViewHolder {
        private final TextView hourValInShortCategoryStats;
        private final TextView minValInShortCategoryStats;
        private final TextView categoryInShortStatsLabel;
        private final Button moreButton;

        public CategoryIntShortStatsCardHolder(View itemView) {
            super(itemView);
            hourValInShortCategoryStats = itemView.findViewById(R.id.hour_val_in_short_category_stats);
            minValInShortCategoryStats = itemView.findViewById(R.id.min_val_in_short_category_stats);
            categoryInShortStatsLabel = itemView.findViewById(R.id.category_in_short_stats_label);
            moreButton = itemView.findViewById(R.id.more_button_in_short_category_stats);
        }
    }
}
