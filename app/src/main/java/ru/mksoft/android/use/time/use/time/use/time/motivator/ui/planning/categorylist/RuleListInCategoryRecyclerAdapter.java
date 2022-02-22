package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.RuleFormat;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 21.02.2022
 */
public class RuleListInCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Rule> rules;
    private CheckBox chosenRule = null;
    private int chosenRulePosition = -1;

    public RuleListInCategoryRecyclerAdapter(List<Rule> rules) {
        this.rules = rules;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RuleListInCategoryRecyclerAdapter.RuleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_in_category_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        RuleListInCategoryRecyclerAdapter.RuleViewHolder ruleViewHolder = (RuleListInCategoryRecyclerAdapter.RuleViewHolder) holder;
        RuleFormat.ShortHourMinuteFormat hourMinuteFormat = new RuleFormat.ShortHourMinuteFormat(rules.get(position));

        ruleViewHolder.ruleLabel.setText(rules.get(position).getName());
        ruleViewHolder.hours.setText(hourMinuteFormat.getHourString());
        ruleViewHolder.minutes.setText(hourMinuteFormat.getMinuteString());

        int pos = position;
        ruleViewHolder.ruleInListCheckbox.setOnClickListener(view -> {
            if (chosenRule != null) {
                chosenRule.setChecked(false);
            }

            chosenRule = ruleViewHolder.ruleInListCheckbox;
            chosenRulePosition = pos;
        });
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public Rule getChosenRule() {
        if (chosenRulePosition != -1) {
            return rules.get(chosenRulePosition);
        } else {
            return null;
        }
    }

    class RuleViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox ruleInListCheckbox;
        private final TextView ruleLabel;
        private final TextView minutes;
        private final TextView hours;

        public RuleViewHolder(View itemView) {
            super(itemView);
            ruleLabel = itemView.findViewById(R.id.rule_body_label);
            minutes = itemView.findViewById(R.id.minutes_row);
            hours = itemView.findViewById(R.id.hours_row);
            ruleInListCheckbox = itemView.findViewById(R.id.is_rule_chosen_for_category_checkbox);
        }
    }

}
