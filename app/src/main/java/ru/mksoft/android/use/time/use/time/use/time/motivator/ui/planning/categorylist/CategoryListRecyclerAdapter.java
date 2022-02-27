package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentCategoryListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.DatabaseException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListRecyclerAdapter;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelper.PREDEFINED_ID;

/**
 * Адаптер списка категорий.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class CategoryListRecyclerAdapter extends RecyclerView.Adapter<CategoryListRecyclerAdapter.CategoryCardViewHolder> {
    private static final String LOG_TAG = RuleListRecyclerAdapter.class.getSimpleName();

    public static final String EDIT_CATEGORY_DIALOG_RESULT_KEY = "edit_category_dialog_result";
    public static final String CREATED_CATEGORY_DIALOG_RESULT_KEY = "created_category_dialog_result";
    public static final String CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "category_holder_position_in_adapter_result";
    public static final String CATEGORY_ID_RESULT_KEY = "category_id_result";

    private final FragmentCategoryListBinding binding;
    private final FragmentManager fragmentManager;
    private final LifecycleOwner lifecycleOwner;

    private final Context context;
    private final List<Category> categories;

    /**
     * Конструктор.
     *
     * @param fragment   фрагмент
     * @param binding    биндинг фрагмента
     * @param categories список категорий для отображения
     */
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
        holder.ruleLabel.setText(category.getRule().getName());

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
        try {
            DbHelperFactory.getHelper().getCategoryDAO().delete(removingCategory);
        } catch (SQLException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof SQLiteConstraintException) {
                    Log.e(LOG_TAG, "Rule deletion error", e);
                    Toast.makeText(context, R.string.edit_category_unable_delete_used, Toast.LENGTH_LONG).show();
                    return;
                }
                cause = cause.getCause();
            }

            throw new DatabaseException(e);
        }

        categories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // fixme нужен ли дубль getItemCount() ?
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
