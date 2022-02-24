package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditRuleBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule.DAY_TIME_LIMIT_FORMAT;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule.DayOfWeek.*;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.EditTimeLimitFragment.*;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListRecyclerAdapter.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditRuleFragment#} factory method to
 * create an instance of this fragment.
 */
public class EditRuleFragment extends BottomSheetDialogFragment {
    public static final String EDIT_TIME_LIMIT_DIALOG_RESULT_KEY = "edit_time_limit_dialog_result";
    private static final Map<Rule.DayOfWeek, Integer> DAY_OF_WEEK_TEXT_KEYS = new EnumMap<>(Rule.DayOfWeek.class);

    static {
        DAY_OF_WEEK_TEXT_KEYS.put(MONDAY, R.string.edit_time_limit_label_monday);
        DAY_OF_WEEK_TEXT_KEYS.put(TUESDAY, R.string.edit_time_limit_label_tuesday);
        DAY_OF_WEEK_TEXT_KEYS.put(WEDNESDAY, R.string.edit_time_limit_label_wednesday);
        DAY_OF_WEEK_TEXT_KEYS.put(THURSDAY, R.string.edit_time_limit_label_thursday);
        DAY_OF_WEEK_TEXT_KEYS.put(FRIDAY, R.string.edit_time_limit_label_friday);
        DAY_OF_WEEK_TEXT_KEYS.put(SATURDAY, R.string.edit_time_limit_label_saturday);
        DAY_OF_WEEK_TEXT_KEYS.put(SUNDAY, R.string.edit_time_limit_label_sunday);
    }

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
        if (EDIT_RULE_DIALOG_RESULT_KEY.equals(fragmentArgs.getCreateOrAddRule())) {
            try {
                rule = DbHelperFactory.getHelper().getRuleDAO().queryForId(Long.valueOf(fragmentArgs.getRuleId()));
            } catch (SQLException e) {
                //todo Обработать ошибки корректно
                e.printStackTrace();
            }
        }
        final Rule finalRule = rule == null ? new Rule() : rule;

        fillRuleData(finalRule);
        assignListeners(fragmentArgs, finalRule);
    }

    private void fillRuleData(Rule rule) {
        binding.editRuleLabel.setText(rule.getName());
        binding.mondayTimeLimit.setText(rule.getHoursMinutesLimitTime(MONDAY));
        binding.tuesdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.TUESDAY));
        binding.wednesdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.WEDNESDAY));
        binding.thursdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.THURSDAY));
        binding.fridayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.FRIDAY));
        binding.saturdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.SATURDAY));
        binding.sundayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.SUNDAY));
    }

    private void assignListeners(EditRuleFragmentArgs fragmentArgs, Rule finalRule) {
        binding.cancelRuleEditionButton.setOnClickListener(this::cancel);
        binding.confirmRuleEditionButton.setOnClickListener(v -> saveRule(finalRule, fragmentArgs.getCreateOrAddRule(), fragmentArgs.getRuleHolderPosition()));

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        NavHostFragment fragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = fragment.getNavController();
        binding.mondayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, MONDAY));
        binding.tuesdayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, TUESDAY));
        binding.wednesdayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, WEDNESDAY));
        binding.thursdayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, THURSDAY));
        binding.fridayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, FRIDAY));
        binding.saturdayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, SATURDAY));
        binding.sundayTimeCell.setOnClickListener(v -> editTileLimit(navController, finalRule, SUNDAY));

        fragmentManager.setFragmentResultListener(EDIT_TIME_LIMIT_DIALOG_RESULT_KEY, getViewLifecycleOwner(), this::processTimeLimitResult);
    }

    private void editTileLimit(NavController navController, Rule rule, Rule.DayOfWeek dayOfWeek) {
        String ruleDay = getString(R.string.edit_time_limit_label_text, getString(DAY_OF_WEEK_TEXT_KEYS.get(dayOfWeek)));

        String ruleName = rule.getName();
        if (ruleName == null || TextUtils.isEmpty(ruleName)) {
            ruleName = getString(R.string.edit_time_limit_no_name_rule);
        }

        navController.navigate(EditRuleFragmentDirections.actionNavEditRuleToFragmentEditTimeLimit(dayOfWeek.name(), ruleName, ruleDay, rule.getHours(dayOfWeek), rule.getMinutes(dayOfWeek)));
    }

    private void processTimeLimitResult(String requestKey, Bundle result) {
        if (!EDIT_TIME_LIMIT_DIALOG_RESULT_KEY.equals(requestKey)) {
            return;
        }

        TextView dayField = findDayField(Rule.DayOfWeek.valueOf(result.getString(DAY_NAME_KEY)));
        dayField.setText(String.format(Locale.ENGLISH, DAY_TIME_LIMIT_FORMAT, result.getInt(RULE_HOURS_KEY), result.getInt(RULE_MINUTES_KEY)));
    }

    private TextView findDayField(Rule.DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return binding.mondayTimeLimit;
            case TUESDAY:
                return binding.tuesdayTimeLimit;
            case WEDNESDAY:
                return binding.wednesdayTimeLimit;
            case THURSDAY:
                return binding.thursdayTimeLimit;
            case FRIDAY:
                return binding.fridayTimeLimit;
            case SATURDAY:
                return binding.saturdayTimeLimit;
            case SUNDAY:
                return binding.sundayTimeLimit;
            default:
                return null;
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditRuleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void cancel(View view) {
        dismiss();
    }

    private void saveRule(Rule rule, String resultType, Integer positionInAdapter) {
        String ruleName = binding.editRuleLabel.getText().toString().trim();
        if (TextUtils.isEmpty(ruleName)) {
            Toast.makeText(this.getContext(), R.string.edit_category_name_empty_warning, Toast.LENGTH_LONG).show();
            return;
        }

        rule.setName(ruleName);
        rule.setDays(parseRuleTimeLimit());
        try {
            DbHelperFactory.getHelper().getRuleDAO().createOrUpdate(rule);
        } catch (SQLException e) {
            Toast.makeText(this.getContext(), R.string.edit_category_name_exists_warning, Toast.LENGTH_LONG).show();
            return;
        }

        Bundle result = new Bundle();
        result.putLong(RULE_ID_RESULT_KEY, rule.getId());
        result.putInt(RULE_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);

        requireActivity().getSupportFragmentManager().setFragmentResult(resultType, result);
        dismiss();
    }

    private Map<Rule.DayOfWeek, Integer> parseRuleTimeLimit() {
        Map<Rule.DayOfWeek, Integer> timeLimits = new EnumMap<>(Rule.DayOfWeek.class);
        timeLimits.put(MONDAY, parseInputData(binding.mondayTimeLimit));
        timeLimits.put(TUESDAY, parseInputData(binding.tuesdayTimeLimit));
        timeLimits.put(WEDNESDAY, parseInputData(binding.wednesdayTimeLimit));
        timeLimits.put(THURSDAY, parseInputData(binding.thursdayTimeLimit));
        timeLimits.put(FRIDAY, parseInputData(binding.fridayTimeLimit));
        timeLimits.put(SATURDAY, parseInputData(binding.saturdayTimeLimit));
        timeLimits.put(SUNDAY, parseInputData(binding.sundayTimeLimit));
        return timeLimits;
    }

    private static Integer parseInputData(TextView timeLimitField) {
        CharSequence timeLimitFieldText = timeLimitField.getText();
        int hours = Integer.parseInt(String.valueOf(timeLimitFieldText.subSequence(0, 2)));
        int minutes = Integer.parseInt(String.valueOf(timeLimitFieldText.subSequence(3, 5)));
        return hours * 60 + minutes;
    }
}
