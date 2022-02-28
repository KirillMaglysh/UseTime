package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

public class AppListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TRACK_NEW_APP_DIALOG_RESULT_KEY = "track_new_app_dialog_result";
    public static final String UNTRACK_APP_DIALOG_RESULT_KEY = "untrack_app_dialog_result";
    public static final String CHOSEN_CATEGORY_ID_RESULT_KEY = "chosen_category_id";
    public static final String APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "app_holder_position_in_adapter_id";

    private final FragmentAppListBinding binding;
    private final FragmentManager fragmentManager;
    private final LifecycleOwner lifecycleOwner;

    private static final int TYPE_ITEM = 0;
    private static final int APP_CARD_ITEM = 1;
    private final Context context;
    List<UserApp> trackedApps;
    List<UserApp> untrackedApps;

    /**
     * Constructor.
     *
     * @param fragment      parent fragment
     * @param binding       fragment binding
     * @param trackedApps   list of tracked apps
     * @param untrackedApps list of untracked apps
     */
    public AppListRecyclerAdapter(Fragment fragment, FragmentAppListBinding binding,
                                  List<UserApp> trackedApps, List<UserApp> untrackedApps) {
        this.trackedApps = trackedApps;
        this.untrackedApps = untrackedApps;
        this.context = fragment.getContext();
        fragmentManager = fragment.requireActivity().getSupportFragmentManager();
        lifecycleOwner = fragment.getViewLifecycleOwner();
        this.binding = binding;


        fragmentManager.setFragmentResultListener(TRACK_NEW_APP_DIALOG_RESULT_KEY, lifecycleOwner, this::trackNewApp);
        fragmentManager.setFragmentResultListener(UNTRACK_APP_DIALOG_RESULT_KEY, lifecycleOwner, this::untrackApp);
    }

    private void trackNewApp(@NonNull String requestKey, @NonNull Bundle result) {
        if (!TRACK_NEW_APP_DIALOG_RESULT_KEY.equals(requestKey)) {
            return;
        }

        int positionInAdapter = result.getInt(APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY);
        int newArrayPosition = positionInAdapter - 1;
        UserApp addingApp = untrackedApps.get(newArrayPosition);

        addingApp.setIsTracked(true);
        try {
            addingApp.setCategory(DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CHOSEN_CATEGORY_ID_RESULT_KEY)));
            DbHelperFactory.getHelper().getUserAppDAO().update(addingApp);
        } catch (SQLException e) {
            //todo Обработать ошибки корректно
            e.printStackTrace();
        }

        trackedApps.add(addingApp);
        untrackedApps.remove(newArrayPosition);
        notifyItemRemoved(positionInAdapter);
        notifyItemRangeChanged(positionInAdapter, getItemCount());
    }

    private void untrackApp(@NonNull String requestKey, @NonNull Bundle result) {
        if (!UNTRACK_APP_DIALOG_RESULT_KEY.equals(requestKey)) {
            return;
        }

        int newPosition = result.getInt(APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY);
        int newArrayPosition = newPosition - 2 - untrackedApps.size();
        UserApp removingApp = trackedApps.get(newArrayPosition);

        try {
            removingApp.setIsTracked(false);
            DbHelperFactory.getHelper().getUserAppDAO().update(removingApp);
        } catch (SQLException e) {
            //todo Обработать ошибки корректно
            e.printStackTrace();
        }

        untrackedApps.add(removingApp);
        trackedApps.remove(newArrayPosition);
        notifyItemRemoved(newPosition);
        notifyItemRangeChanged(getItemCount() - trackedApps.size() - 2, getItemCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new AppTypeLabel(LayoutInflater.from(context).inflate(R.layout.app_list_type, parent, false));
        } else {
            return new AppCardViewHolder(LayoutInflater.from(context).inflate(R.layout.app_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0 || position == untrackedApps.size() + 1) {
            bindLabel((AppTypeLabel) holder, position);
        } else {
            ApplicationInfo applicationInfo = null;
            boolean isUntracked = position <= untrackedApps.size();
            int arrayPosition = isUntracked ? position - 1 : position - 2 - untrackedApps.size();
            String packageName = isUntracked ? untrackedApps.get(arrayPosition).getPackageName() : trackedApps.get(arrayPosition).getPackageName();

            PackageManager packageManager = context.getPackageManager();
            try {
                applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                //todo Обработать ошибки корректно
                e.printStackTrace();
            }

            AppCardViewHolder appCardHolder = (AppCardViewHolder) holder;
            appCardHolder.appLabel.setText(packageManager.getApplicationLabel(applicationInfo));
            appCardHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));

            if (isUntracked) {
                bindUntrackedApp(appCardHolder);
            } else {
                bindTrackedApp(appCardHolder);
            }
        }
    }

    private void bindTrackedApp(AppCardViewHolder appCardHolder) {
        ((MaterialButton) appCardHolder.appActionButton).setIcon(context.getDrawable(R.drawable.ic_favorite_filled));
        ((MaterialButton) appCardHolder.appActionButton).setIconTintMode(PorterDuff.Mode.DST_IN);
        appCardHolder.appCategory.setText(trackedApps.get(appCardHolder.getAdapterPosition() - 2 - untrackedApps.size()).getCategory().getName());

        appCardHolder.appActionButton.setOnClickListener(view -> Navigation.findNavController(appCardHolder.itemView)
                .navigate(AppListFragmentDirections.actionNavApplistToNavUntrackAppDialog(
                        appCardHolder.appLabel.getText().toString(), appCardHolder.getAdapterPosition())));
    }

    private void bindUntrackedApp(AppCardViewHolder appCardHolder) {
        ((MaterialButton) appCardHolder.appActionButton).setIcon(context.getDrawable(R.drawable.ic_favorite));
        ((MaterialButton) appCardHolder.appActionButton).setIconTintMode(PorterDuff.Mode.SRC_IN);
        appCardHolder.appCategory.setText("");

        appCardHolder.appActionButton.setOnClickListener(view -> Navigation.findNavController(appCardHolder.itemView)
                .navigate(AppListFragmentDirections.actionNavApplistToNavTrackNewAppDialog(
                        appCardHolder.appLabel.getText().toString(), appCardHolder.getAdapterPosition())));
    }

    private void bindLabel(AppTypeLabel holder, int position) {
        int appTypeLabel = position == 0 ? R.string.untracked_app : R.string.tracked_apps;
        holder.typeLabel.setText(appTypeLabel);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 || position == untrackedApps.size() + 1) ? TYPE_ITEM : APP_CARD_ITEM;
    }

    @Override
    public int getItemCount() {
        return untrackedApps.size() + trackedApps.size() + 2;
    }

    class AppTypeLabel extends RecyclerView.ViewHolder {
        TextView typeLabel;

        public AppTypeLabel(@NonNull View itemView) {
            super(itemView);
            typeLabel = itemView.findViewById(R.id.app_list_type);
        }
    }

    class AppCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView appLabel;
        private final ImageView appIcon;
        private final TextView appCategory;
        private final Button appActionButton;

        public AppCardViewHolder(View itemView) {
            super(itemView);
            appLabel = itemView.findViewById(R.id.app_label);
            appIcon = itemView.findViewById(R.id.app_icon);
            appCategory = itemView.findViewById(R.id.app_category);
            appActionButton = itemView.findViewById(R.id.app_card_action_button);
        }
    }
}
