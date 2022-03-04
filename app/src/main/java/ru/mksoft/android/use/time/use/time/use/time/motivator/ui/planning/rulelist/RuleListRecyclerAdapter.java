package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentRuleListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.DatabaseException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.messaging.MessageDialogType;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.RuleViewHolder;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelper.PREDEFINED_ID;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

public class RuleListRecyclerAdapter extends RecyclerView.Adapter<RuleListRecyclerAdapter.RuleCardViewHolder> {
    private static final String LOG_TAG = RuleListRecyclerAdapter.class.getSimpleName();

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
                //todo Обработать ошибки корректно
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
                //todo Обработать ошибки корректно
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
        Rule rule = rules.get(holder.getAdapterPosition());
        holder.fillRuleData(rule);

        if (PREDEFINED_ID == rule.getId()) {
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            holder.editButton.setOnClickListener(view -> editRule(holder));
            holder.deleteButton.setOnClickListener(view -> deleteRule(view, holder.getAdapterPosition()));
        }
    }

    private void editRule(RuleCardViewHolder holder) {
        int position = holder.getAdapterPosition();
        Rule rule = rules.get(position);
        Navigation.findNavController(holder.itemView)
                .navigate(RuleListFragmentDirections.actionNavRuleListToNavEditRule(position,
                        rule.getId().toString(), EDIT_RULE_DIALOG_RESULT_KEY));
    }

    private void deleteRule(View view, int position) {
        Rule removingRule = rules.get(position);
        try {
            DbHelperFactory.getHelper().getRuleDAO().delete(removingRule);
        } catch (SQLException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof SQLiteConstraintException) {
                    Log.e(LOG_TAG, "Rule deletion error", e);
                    Navigation.findNavController(view)
                            .navigate(RuleListFragmentDirections.actionNavRuleListToNavMessageDialog(
                                    MessageDialogType.ERROR,
                                    null,
                                    context.getString(R.string.edit_rule_unable_delete_used),
                                    null,
                                    null
                            ));
                    return;
                }
                cause = cause.getCause();
            }

            throw new DatabaseException(e);
        }

        rules.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public int getRuleNum() {
        return rules.size();
    }

    static final class RuleCardViewHolder extends RuleViewHolder {
        private final Button editButton;
        private final Button deleteButton;

        public RuleCardViewHolder(View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.rule_edit_button);
            deleteButton = itemView.findViewById(R.id.rule_delete_button);
        }
    }
}
