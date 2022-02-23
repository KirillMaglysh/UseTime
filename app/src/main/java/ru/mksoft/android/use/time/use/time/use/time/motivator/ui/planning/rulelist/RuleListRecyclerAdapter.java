package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

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
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentRuleListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

public class RuleListRecyclerAdapter extends RecyclerView.Adapter<RuleListRecyclerAdapter.RuleCardViewHolder> {
    public static final String EDIT_RULE_DIALOG_RESULT_KEY = "edit_rule_dialog_result";
    public static final String CREATED_RULE_DIALOG_RESULT_KEY = "created_rule_dialog_result";
    public static final String RULE_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "rule_holder_position_in_adapter_result";
    public static final String RULE_ID_RESULT_KEY = "rule_id_result";

    private final FragmentRuleListBinding binding;
    private final FragmentManager fragmentManager;
    private final LifecycleOwner lifecycleOwner;

    private final Context context;
    private List<Rule> rules;

    public RuleListRecyclerAdapter(Fragment fragment, FragmentRuleListBinding binding,
                                   List<Rule> rules) {
        this.rules = rules;
        this.context = fragment.getContext();
        fragmentManager = fragment.requireActivity().getSupportFragmentManager();
        lifecycleOwner = fragment.getViewLifecycleOwner();
        this.binding = binding;

        fragmentManager.setFragmentResultListener(EDIT_RULE_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!EDIT_RULE_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            int position = result.getInt(RULE_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY);
            try {
                rules.set(position, DbHelperFactory.getHelper().getRuleDAO().queryForId(result.getLong(RULE_ID_RESULT_KEY)));
                notifyItemChanged(position);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        fragmentManager.setFragmentResultListener(CREATED_RULE_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!CREATED_RULE_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            try {
                rules.add(DbHelperFactory.getHelper().getRuleDAO().queryForId(result.getLong(RULE_ID_RESULT_KEY)));
                notifyItemInserted(rules.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    @Override
    public RuleCardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RuleCardViewHolder(LayoutInflater.from(context).inflate(R.layout.rule_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RuleCardViewHolder holder, int position) {
        Rule rule = rules.get(position);
        holder.ruleLabel.setText(rule.getName());
        holder.mondayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.MONDAY));
        holder.tuesdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.TUESDAY));
        holder.wednesdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.WEDNESDAY));
        holder.thursdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.THURSDAY));
        holder.fridayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.FRIDAY));
        holder.saturdayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.SATURDAY));
        holder.sundayTimeLimit.setText(rule.getHoursMinutesLimitTime(Rule.DayOfWeek.SUNDAY));

        holder.editButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                .navigate(RuleListFragmentDirections.actionNavRuleListToNavEditRule(position,
                        rule.getId().toString(), EDIT_RULE_DIALOG_RESULT_KEY)));

        holder.deleteButton.setOnClickListener(view -> {
            Rule removingRule = rule;
            rules.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            try {
                DbHelperFactory.getHelper().getRuleDAO().delete(removingRule);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public int getRuleNum() {
        return rules.size();
    }

    class RuleCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleLabel;
        private final TextView mondayTimeLimit;
        private final TextView tuesdayTimeLimit;
        private final TextView wednesdayTimeLimit;
        private final TextView thursdayTimeLimit;
        private final TextView fridayTimeLimit;
        private final TextView saturdayTimeLimit;
        private final TextView sundayTimeLimit;
        private final Button editButton;
        private final Button deleteButton;

        public RuleCardViewHolder(View itemView) {
            super(itemView);
            ruleLabel = itemView.findViewById(R.id.rule_card_title);
            mondayTimeLimit = itemView.findViewById(R.id.monday_time_limit);
            tuesdayTimeLimit = itemView.findViewById(R.id.tuesday_time_limit);
            wednesdayTimeLimit = itemView.findViewById(R.id.wednesday_time_limit);
            thursdayTimeLimit = itemView.findViewById(R.id.thursday_time_limit);
            fridayTimeLimit = itemView.findViewById(R.id.friday_time_limit);
            saturdayTimeLimit = itemView.findViewById(R.id.saturday_time_limit);
            sundayTimeLimit = itemView.findViewById(R.id.sunday_time_limit);
            editButton = itemView.findViewById(R.id.rule_edit_button);
            deleteButton = itemView.findViewById(R.id.rule_delete_button);
        }
    }
}
