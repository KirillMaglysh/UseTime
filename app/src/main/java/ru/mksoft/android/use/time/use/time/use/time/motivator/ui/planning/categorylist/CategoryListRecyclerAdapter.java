package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentCategoryListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.RuleFormat;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class CategoryListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String EDIT_CATEGORY_DIALOG_RESULT_KEY = "edit_category_dialog_result";
    public static final String CREATED_CATEGORY_DIALOG_RESULT_KEY = "created_category_dialog_result";
    public static final String CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "category_holder_position_in_adapter_result";
    public static final String CATEGORY_ID_RESULT_KEY = "category_id_result";

    private final FragmentCategoryListBinding binding;
    private final FragmentManager fragmentManager;
    private final LifecycleOwner lifecycleOwner;

    private final Context context;
    private List<Category> categories;

    public CategoryListRecyclerAdapter(Fragment fragment, FragmentCategoryListBinding binding,
                                       List<Category> categories) {
        this.categories = categories;
        this.context = fragment.getContext();
        fragmentManager = fragment.requireActivity().getSupportFragmentManager();
        lifecycleOwner = fragment.getViewLifecycleOwner();
        this.binding = binding;

        fragmentManager.setFragmentResultListener(EDIT_CATEGORY_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!EDIT_CATEGORY_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            int position = result.getInt(CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY);
            try {
                categories.set(position, DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CATEGORY_ID_RESULT_KEY)));
                notifyItemChanged(position);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        fragmentManager.setFragmentResultListener(CREATED_CATEGORY_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!CREATED_CATEGORY_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            try {
                categories.add(DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CATEGORY_ID_RESULT_KEY)));
                notifyItemInserted(categories.size() - 1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CategoryListRecyclerAdapter.CategoryCardViewHolder(LayoutInflater.from(context).inflate(R.layout.category_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        CategoryCardViewHolder cardViewHolder = (CategoryCardViewHolder) holder;
        Rule rule = null;
        try {
            rule = DbHelperFactory.getHelper().getRuleDAO().queryForId(categories.get(position).getRuleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RuleFormat.ShortHourMinuteFormat hourMinuteFormat = new RuleFormat.ShortHourMinuteFormat(rule);
        cardViewHolder.hours.setText(hourMinuteFormat.getHourString());
        cardViewHolder.minutes.setText(hourMinuteFormat.getMinuteString());
        cardViewHolder.categoryLabel.setText(categories.get(position).getName());

        cardViewHolder.ruleLabel.setText(rule.getName());

        cardViewHolder.editButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavEditCategory(position,
                        categories.get(position).getId().toString(), EDIT_CATEGORY_DIALOG_RESULT_KEY)));

        cardViewHolder.deleteButton.setOnClickListener(view -> {
            Category removingCategory = categories.get(position);
            categories.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            try {
                DbHelperFactory.getHelper().getCategoryDAO().delete(removingCategory);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public int getCategoryNum() {
        return categories.size();
    }

    class CategoryCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryLabel;
        private final TextView ruleLabel;
        private final TextView minutes;
        private final TextView hours;
        private final Button editButton;
        private final Button deleteButton;

        public CategoryCardViewHolder(View itemView) {
            super(itemView);
            categoryLabel = itemView.findViewById(R.id.category_card_label);
            ruleLabel = itemView.findViewById(R.id.rule_body_label);
            minutes = itemView.findViewById(R.id.minutes_row);
            hours = itemView.findViewById(R.id.hours_row);
            editButton = itemView.findViewById(R.id.category_edit_button);
            deleteButton = itemView.findViewById(R.id.category_delete_button);
        }
    }
}
