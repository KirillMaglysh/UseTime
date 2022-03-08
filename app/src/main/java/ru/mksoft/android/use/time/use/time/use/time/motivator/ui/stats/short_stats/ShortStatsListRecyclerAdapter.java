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

import java.util.List;
import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class ShortStatsListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TIME_PART_FORMAT = "%02d";

    private List<ShortUIStatsListFragment.CategoryInShortSummary> categoriesInShortSummary;
    private Context context;

    public ShortStatsListRecyclerAdapter(Context context, List<ShortUIStatsListFragment.CategoryInShortSummary> categoriesInShortSummary) {
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
        ShortUIStatsListFragment.CategoryInShortSummary categoryStats = categoriesInShortSummary.get(position);
        cardHolder.hourValInShortCategoryStats.setText(String.format(Locale.US, TIME_PART_FORMAT, categoryStats.getHours()));
        cardHolder.minValInShortCategoryStats.setText(String.format(Locale.US, TIME_PART_FORMAT, categoryStats.getMinutes()));
        cardHolder.categoryInShortStatsLabel.setText(categoryStats.getCategory().getName());

        ((CategoryIntShortStatsCardHolder) holder).moreButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                .navigate(ShortUIStatsListFragmentDirections.actionNavShortStatsListToNavFullStats(categoryStats.getCategory().getId().toString())));
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
            minValInShortCategoryStats = itemView.findViewById(R.id.minute_val_in_short_category_stats);
            categoryInShortStatsLabel = itemView.findViewById(R.id.category_in_short_stats_label);
            moreButton = itemView.findViewById(R.id.more_button_in_short_category_stats);
        }
    }
}
