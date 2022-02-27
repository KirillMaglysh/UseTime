package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentRuleListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListRecyclerAdapter.CREATED_RULE_DIALOG_RESULT_KEY;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RuleListFragment extends Fragment {
    private FragmentRuleListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRuleListBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.ruleListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RuleListRecyclerAdapter adapter = null;
        try {
            adapter = new RuleListRecyclerAdapter(this, binding, DbHelperFactory.getHelper().getRuleDAO().getAllRules());
            recyclerView.setAdapter(adapter);
        } catch (SQLException e) {
            //todo Обработать ошибки корректно
            e.printStackTrace();
        }

        RuleListRecyclerAdapter finalAdapter = adapter;
        binding.newRuleButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(RuleListFragmentDirections.actionNavRuleListToNavEditRule(finalAdapter.getRuleNum(), "-1", CREATED_RULE_DIALOG_RESULT_KEY)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}