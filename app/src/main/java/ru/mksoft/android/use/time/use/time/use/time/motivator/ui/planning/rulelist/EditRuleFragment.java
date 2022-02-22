package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditRuleBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.RuleFormat;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListRecyclerAdapter.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditRuleFragment#} factory method to
 * create an instance of this fragment.
 */
public class EditRuleFragment extends BottomSheetDialogFragment {
    private FragmentEditRuleBinding binding;

    public EditRuleFragment() {
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
        EditRuleFragmentArgs fragmentArgs = EditRuleFragmentArgs.fromBundle(getArguments());
        Rule rule = null;
        try {
            rule = EDIT_RULE_DIALOG_RESULT_KEY.equals(fragmentArgs.getCreateOrAddRule()) ?
                    DbHelperFactory.getHelper().getRuleDAO().queryForId(Long.valueOf(fragmentArgs.getRuleId())) :
                    new Rule();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        binding.cancelRuleEditionButton.setOnClickListener(this::cancel);
        final Rule finalRule = rule;

        binding.confirmRuleEditionButton.setOnClickListener(v -> add(finalRule, fragmentArgs.getCreateOrAddRule(), fragmentArgs.getRuleHolderPosition()));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditRuleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void add(Rule rule, String resultType, Integer positionInAdapter) {
        rule.setName(binding.editRuleLabel.getText().toString());
        rule.setDays(RuleFormat.parseRuleInput(binding));
        try {
            DbHelperFactory.getHelper().getRuleDAO().createOrUpdate(rule);
        } catch (SQLException e) {
            Toast.makeText(this.getContext(), "The name is already exist", Toast.LENGTH_LONG).show();
            return;
        }

        Bundle result = new Bundle();
        result.putLong(RULE_ID_RESULT_KEY, rule.getId());
        result.putInt(RULE_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);

        requireActivity().getSupportFragmentManager().setFragmentResult(resultType, result);
        dismiss();
    }

    private void cancel(View view) {
        dismiss();
    }

/*
    private void hideKeyBoard() {
        View view = ((Activity) Objects.requireNonNull(getContext())).getCurrentFocus();
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
*/
}
