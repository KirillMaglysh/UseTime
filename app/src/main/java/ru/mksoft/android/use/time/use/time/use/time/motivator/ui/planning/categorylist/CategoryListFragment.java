package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentCategoryListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListFragmentDirections;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter.CREATED_CATEGORY_DIALOG_RESULT_KEY;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoryListFragment extends Fragment {
    private FragmentCategoryListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.categoryListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryListRecyclerAdapter adapter = null;
        try {
            adapter = new CategoryListRecyclerAdapter(this, binding, DbHelperFactory.getHelper().getCategoryDAO().getAllCategories());
            recyclerView.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        CategoryListRecyclerAdapter finalAdapter = adapter;
        binding.newCategoryButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavEditCategory(finalAdapter.getCategoryNum(), "-1", CREATED_CATEGORY_DIALOG_RESULT_KEY)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
