package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;
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

//TODO() передавать список приложений с иконками и так чтобы можно было добавлять и убирать из базы отслеживаемые приложения
//public class AppListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
public class AppListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TrackNewAppDialog.AppCategoryChosenListener {
    public static final String HANDLER_FOR_TRACK_DIALOG_KEY = "HANDLER_FOR_TRACK_DIALOG_KEY";

    private static final int TYPE_ITEM = 0;
    private static final int APP_CARD_ITEM = 1;
    Context context;
    List<UserApp> trackedApps;
    List<UserApp> untrackedApps;

    public AppListRecyclerAdapter(Context context,
                                  List<UserApp> trackedApps, List<UserApp> untrackedApps) {
        this.trackedApps = trackedApps;
        this.untrackedApps = untrackedApps;
        this.context = context;

        Storage.getInstance().setAppListRecyclerAdapter(this);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new AppTypeLabel(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_type, parent, false));
        } else {
            return new AppCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
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
                e.printStackTrace();
            }

            AppCardViewHolder appCardHolder = (AppCardViewHolder) holder;
            appCardHolder.appLabel.setText(packageManager.getApplicationLabel(applicationInfo));
            appCardHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));

            if (isUntracked) {
                bindUntrackedApp(appCardHolder);
            } else {
                binTrackedApp(appCardHolder);
            }

            if (position > untrackedApps.size()) {
                appCardHolder.appCategory.setText(trackedApps.get(position - 2 - untrackedApps.size()).getCategory().getName());
            }
        }
    }

    private void binTrackedApp(AppCardViewHolder appCardHolder) {
        appCardHolder.appDeleteButton.setText("DEL");
        appCardHolder.appDeleteButton.setOnClickListener(view -> {
            int newPosition = appCardHolder.getAdapterPosition();
            int newArrayPosition = newPosition - 2 - untrackedApps.size();
            UserApp removingApp = trackedApps.get(newArrayPosition);
            untrackedApps.add(removingApp);
            trackedApps.remove(newArrayPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, getItemCount());

            try {
                removingApp.setIsTracked(false);
                DbHelperFactory.getHelper().getUserAppDAO().update(removingApp);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void bindUntrackedApp(AppCardViewHolder appCardHolder) {
        appCardHolder.appDeleteButton.setText("ADD");

        appCardHolder.appDeleteButton.setOnClickListener(view -> Navigation.findNavController(appCardHolder.itemView)
                .navigate(AppListFragmentDirections.actionNavApplistToNavTrackNewAppDialog(
                        appCardHolder.appLabel.getText().toString(), appCardHolder.getAdapterPosition())));
    }

    private void bindLabel(AppTypeLabel holder, int position) {
        if (position == 0) {
            holder.typeLabel.setText("НЕОТСЛЕЖИВАЕМЫЕ ПРИЛОЖЕНИЯ");
        } else {
            holder.typeLabel.setText("ОТСЛЕЖИВАЕМЫЕ ПРИЛОЖЕНИЯ");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 || position == untrackedApps.size() + 1) ? TYPE_ITEM : APP_CARD_ITEM;
    }

    @Override
    public int getItemCount() {
        return untrackedApps.size() + trackedApps.size() + 2;
    }

    @Override
    public void processChosenCategory(Category category, Integer positionInAdapter) {
        int newArrayPosition = positionInAdapter - 1;
        UserApp addingApp = untrackedApps.get(newArrayPosition);
        trackedApps.add(addingApp);
        untrackedApps.remove(newArrayPosition);
        notifyItemRemoved(positionInAdapter);
        notifyItemRangeChanged(positionInAdapter, getItemCount());

        addingApp.setIsTracked(true);

        try {
            addingApp.setCategory(category);
            DbHelperFactory.getHelper().getUserAppDAO().update(addingApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class AppTypeLabel extends RecyclerView.ViewHolder {
        TextView typeLabel;

        public AppTypeLabel(@NonNull @NotNull View itemView) {
            super(itemView);
            typeLabel = itemView.findViewById(R.id.app_list_type);
        }
    }

    class AppCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView appLabel;
        private final ImageView appIcon;
        private final TextView appCategory;
        private final Button appDeleteButton;

        public AppCardViewHolder(View itemView) {
            super(itemView);
            appLabel = itemView.findViewById(R.id.app_label);
            appIcon = itemView.findViewById(R.id.app_icon);
            appCategory = itemView.findViewById(R.id.app_category);
            appDeleteButton = itemView.findViewById(R.id.app_add_delete_card);
        }
    }
}
