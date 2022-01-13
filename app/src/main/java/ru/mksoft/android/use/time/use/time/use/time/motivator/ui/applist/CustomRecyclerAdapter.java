package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UserApp;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

//TODO() передавать список приложений с иконками и так чтобы можно было добавлять и убирать из базы отслеживаемые приложения
public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int APP_CARD_ITEM = 1;
    List<UserApp> trackedApps;
    List<UserApp> untrackedApps;
    PackageManager packageManager;

    public CustomRecyclerAdapter(PackageManager packageManager, List<UserApp> trackedApps, List<UserApp> untrackedApps) {
        this.trackedApps = trackedApps;
        this.untrackedApps = untrackedApps;
        this.packageManager = packageManager;
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
            AppTypeLabel appTypeLabelHolder = (AppTypeLabel) holder;
            if (position == 0) {
                appTypeLabelHolder.typeLabel.setText("НЕОТСЛЕЖИВАЕМЫЕ ПРИЛОЖЕНИЯ");
            } else {
                appTypeLabelHolder.typeLabel.setText("ОТСЛЕЖИВАЕМЫЕ ПРИЛОЖЕНИЯ");
            }
        } else {
            ApplicationInfo applicationInfo = null;
            String packageName = position <= untrackedApps.size() ? untrackedApps.get(position - 1).getPackageName() : trackedApps.get(position - 2 - untrackedApps.size()).getPackageName();

            try {
                applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            AppCardViewHolder appCardHolder = (AppCardViewHolder) holder;
            appCardHolder.appLabel.setText(packageManager.getApplicationLabel(applicationInfo));
            appCardHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
            if (position > untrackedApps.size()) {
                appCardHolder.appCategory.setText(trackedApps.get(position - 2 - untrackedApps.size()).getCategory().getName());
            }
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


    class AppTypeLabel extends RecyclerView.ViewHolder {
        TextView typeLabel;

        public AppTypeLabel(@NonNull @NotNull View itemView) {
            super(itemView);
            typeLabel = itemView.findViewById(R.id.app_list_type);
        }
    }

    class AppCardViewHolder extends RecyclerView.ViewHolder {
        TextView appLabel;
        ImageView appIcon;
        TextView appCategory;

        public AppCardViewHolder(View itemView) {
            super(itemView);
            appLabel = itemView.findViewById(R.id.app_label);
            appIcon = itemView.findViewById(R.id.app_icon);
            appCategory = itemView.findViewById(R.id.app_category);
        }
    }
}
