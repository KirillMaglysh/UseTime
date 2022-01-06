package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.graphics.drawable.Drawable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Category;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 06.01.2022
 */
public class AppCardInfo {
    String label;
    Drawable icon;
//    Category category;
    String category;

    public AppCardInfo(String label, Drawable icon, String category) {
        this.label = label;
        this.icon = icon;
        this.category = category;
    }
}
