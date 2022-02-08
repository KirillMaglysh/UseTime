package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 06.02.2022
 */
public class CategoryListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Category> categories;
    private CheckBox chosenCategory = null;
    private int chosenCategoryPosition = -1;

    public CategoryListRecyclerAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_in_dialog_label, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        categoryViewHolder.categoryInListCheckbox.setText(categories.get(position).getName());

        int pos = position;
        categoryViewHolder.categoryInListCheckbox.setOnClickListener(view -> {
            if (chosenCategory != null) {
                chosenCategory.setChecked(false);
            }

            chosenCategory = categoryViewHolder.categoryInListCheckbox;
            chosenCategoryPosition = pos;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Category getChosenCategory() {
        if (chosenCategoryPosition != -1) {
            return categories.get(chosenCategoryPosition);
        } else {
            return null;
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox categoryInListCheckbox;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryInListCheckbox = itemView.findViewById(R.id.category_in_dialog_label);
        }
    }
}
