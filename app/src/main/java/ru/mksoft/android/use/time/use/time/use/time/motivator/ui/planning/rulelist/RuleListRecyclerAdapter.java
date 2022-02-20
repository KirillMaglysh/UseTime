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
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.RuleFormat;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

public class RuleListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                notifyItemInserted(rules.size() - 1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RuleCardViewHolder(LayoutInflater.from(context).inflate(R.layout.rule_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        RuleFormat.ShortHourMinuteFormat hourMinuteFormat = new RuleFormat.ShortHourMinuteFormat(rules.get(position));
        ((RuleCardViewHolder) holder).hours.setText(hourMinuteFormat.getHourString());
        ((RuleCardViewHolder) holder).minutes.setText(hourMinuteFormat.getMinuteString());

        ((RuleCardViewHolder) holder).editButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                .navigate(RuleListFragmentDirections.actionNavRuleListToNavEditRule(position,
                        rules.get(position).getId().toString(), EDIT_RULE_DIALOG_RESULT_KEY)));

        ((RuleCardViewHolder) holder).deleteButton.setOnClickListener(view -> {
            Rule removingRule = rules.get(position);
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
        private final TextView minutes;
        private final TextView hours;
        private final Button editButton;
        private final Button deleteButton;

        public RuleCardViewHolder(View itemView) {
            super(itemView);
            ruleLabel = itemView.findViewById(R.id.rule_card_label);
            minutes = itemView.findViewById(R.id.minutes_row);
            hours = itemView.findViewById(R.id.hours_row);
            editButton = itemView.findViewById(R.id.rule_edit_button);
            deleteButton = itemView.findViewById(R.id.rule_delete_button);
        }
    }
}
