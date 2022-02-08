package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

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
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentTrackNewAppDialogBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackNewAppDialog#} factory method to
 * create an instance of this fragment.
 */
public class TrackNewAppDialog extends BottomSheetDialogFragment {
    private FragmentTrackNewAppDialogBinding binding;
    private AppCategoryChosenListener listener;


    public TrackNewAppDialog() {
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
        TrackNewAppDialogArgs fragmentArgs = TrackNewAppDialogArgs.fromBundle(getArguments());
        listener = Storage.getInstance().getAppListRecyclerAdapter();

        binding.dialogAppName.setText(fragmentArgs.getAppLabel());

        RecyclerView recyclerView = binding.categoryListInDialog;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryListRecyclerAdapter categoryListAdapter = null;
        try {
            categoryListAdapter = new CategoryListRecyclerAdapter(DbHelperFactory.getHelper().getCategoryDAO().getAllCategories());
            recyclerView.setAdapter(categoryListAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final CategoryListRecyclerAdapter adapter = categoryListAdapter;
        binding.cancelDialogButton.setOnClickListener(this::cancel);
        binding.addDialogButton.setOnClickListener(v -> {
            if (adapter != null) {
                add(adapter.getChosenCategory(), fragmentArgs.getPositionInAdapter());
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackNewAppDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void add(Category category, Integer positionInAdapter) {
        if (category != null) {
            publish(category, positionInAdapter);
            dismiss();
        } else {
            Toast.makeText(this.getContext(), "Chose category", Toast.LENGTH_LONG).show();
        }
    }

    private void cancel(View view) {
        dismiss();
    }

    public void publish(Category category, int positionInAdapter) {
        listener.processChosenCategory(category, positionInAdapter);
    }

    public interface AppCategoryChosenListener {
        void processChosenCategory(Category category, Integer positionInAdapter);
    }
}
