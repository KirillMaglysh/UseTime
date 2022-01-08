package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.TrackedApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.UntrackedApp;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.01.2022
 */

//TODO() передавать список приложений с иконками и так чтобы можно было добавлять и убирать из базы отслеживаемые приложения
public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder> {
    List<TrackedApp> trackedApps;
    List<UntrackedApp> untrackedApps;

    public CustomRecyclerAdapter(List<TrackedApp> trackedApps, List<UntrackedApp> untrackedApps) {
        this.trackedApps = trackedApps;
        this.untrackedApps = untrackedApps;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.appLabel.setText(untrackedApps.get(position).getName());
        holder.appIcon.setImageDrawable(appCards.get(position).icon);
        holder.appCategory.setText(untrackedApps.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return untrackedApps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView appLabel;
        ImageView appIcon;
        TextView appCategory;

        public MyViewHolder(View itemView) {
            super(itemView);
            appLabel = itemView.findViewById(R.id.app_label);
            appIcon = itemView.findViewById(R.id.app_icon);
            appCategory = itemView.findViewById(R.id.app_category);
        }
    }
}
