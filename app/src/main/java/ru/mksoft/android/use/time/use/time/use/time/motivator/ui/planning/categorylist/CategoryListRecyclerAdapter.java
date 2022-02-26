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
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentCategoryListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelper.PREDEFINED_ID;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class CategoryListRecyclerAdapter extends RecyclerView.Adapter<CategoryListRecyclerAdapter.CategoryCardViewHolder> {
    public static final String EDIT_CATEGORY_DIALOG_RESULT_KEY = "edit_category_dialog_result";
    public static final String CREATED_CATEGORY_DIALOG_RESULT_KEY = "created_category_dialog_result";
    public static final String CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "category_holder_position_in_adapter_result";
    public static final String CATEGORY_ID_RESULT_KEY = "category_id_result";

    private final FragmentCategoryListBinding binding;
    private final FragmentManager fragmentManager;
    private final LifecycleOwner lifecycleOwner;

    private final Context context;
    private List<Category> categories;

    public CategoryListRecyclerAdapter(Fragment fragment, FragmentCategoryListBinding binding, List<Category> categories) {
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
                //todo Обработать ошибки корректно
                e.printStackTrace();
            }
        });

        fragmentManager.setFragmentResultListener(CREATED_CATEGORY_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!CREATED_CATEGORY_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            try {
                categories.add(DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CATEGORY_ID_RESULT_KEY)));
                notifyItemInserted(categories.size());
            } catch (SQLException e) {
                //todo Обработать ошибки корректно
                e.printStackTrace();
            }
        });
    }

    @NonNull
    @Override
    public CategoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        return new CategoryListRecyclerAdapter.CategoryCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCardViewHolder holder, int position) {
        Category category = categories.get(holder.getAdapterPosition());
        holder.categoryTitle.setText(category.getName());

        try {
            Rule rule = DbHelperFactory.getHelper().getRuleDAO().queryForId(category.getRule().getId());
            holder.ruleLabel.setText(rule.getName());
        } catch (SQLException e) {
            //todo Обработать ошибки корректно
            e.printStackTrace();
        }

        if (PREDEFINED_ID == category.getId()) {
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            holder.editButton.setOnClickListener(view -> editCategory(holder));
            holder.deleteButton.setOnClickListener(view -> deleteCategory(holder.getAdapterPosition()));
        }
    }

    private void editCategory(CategoryCardViewHolder holder) {
        int position = holder.getAdapterPosition();
        Navigation.findNavController(holder.itemView)
                .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavEditCategory(position,
                        categories.get(position).getId().toString(), EDIT_CATEGORY_DIALOG_RESULT_KEY));
    }

    private void deleteCategory(int position) {
        Category removingCategory = categories.get(position);
        categories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        try {
            DbHelperFactory.getHelper().getCategoryDAO().delete(removingCategory);
        } catch (SQLException e) {
            //todo Обработать ошибки корректно
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public int getCategoryNum() {
        return categories.size();
    }

    class CategoryCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;
        private final TextView ruleLabel;
        private final Button editButton;
        private final Button deleteButton;

        public CategoryCardViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_card_title);
            ruleLabel = itemView.findViewById(R.id.category_card_rule_name);
            editButton = itemView.findViewById(R.id.category_card_edit_button);
            deleteButton = itemView.findViewById(R.id.category_card_delete_button);
        }
    }
}
