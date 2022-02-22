package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditCategoryBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditCategoryFragment#} factory method to
 * create an instance of this fragment.
 */
public class EditCategoryFragment extends BottomSheetDialogFragment {
    private FragmentEditCategoryBinding binding;

    public EditCategoryFragment() {
        setCancelable(false);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrackNewAppDialog.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditCategoryFragmentArgs fragmentArgs = EditCategoryFragmentArgs.fromBundle(getArguments());
        Category category = null;
        try {
            category = EDIT_CATEGORY_DIALOG_RESULT_KEY.equals(fragmentArgs.getCreateOrAddCategory()) ?
                    DbHelperFactory.getHelper().getCategoryDAO().queryForId(Long.valueOf(fragmentArgs.getCategoryId())) :
                    new Category();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = binding.ruleListInCategoryDialog;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RuleListInCategoryRecyclerAdapter ruleListAdapter = null;
        try {
            ruleListAdapter = new RuleListInCategoryRecyclerAdapter(DbHelperFactory.getHelper().getRuleDAO().getAllRules());
            recyclerView.setAdapter(ruleListAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final Category finalCategory = category;
        final RuleListInCategoryRecyclerAdapter adapter = ruleListAdapter;
        binding.confirmCategoryDialogButton.setOnClickListener(v -> {
            if (adapter != null) {
                add(finalCategory, adapter.getChosenRule(), fragmentArgs.getCreateOrAddCategory(), fragmentArgs.getCategoryHolderPosition());
            }
        });


        binding.cancelCategoryDialogButton.setOnClickListener(this::cancel);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void add(Category category, Rule rule, String resultType, Integer positionInAdapter) {
        if (rule != null) {
            category.setName(binding.categoryLabel.getText().toString());
            category.setRuleId(rule.getId());
            try {
                DbHelperFactory.getHelper().getCategoryDAO().createOrUpdate(category);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bundle result = new Bundle();
            result.putLong(CATEGORY_ID_RESULT_KEY, category.getId());
            result.putInt(CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);

            requireActivity().getSupportFragmentManager().setFragmentResult(resultType, result);
            dismiss();
        } else {
            Toast.makeText(this.getContext(), "Chose rule", Toast.LENGTH_LONG).show();
        }
    }

    private void cancel(View view) {
        dismiss();
    }
}
