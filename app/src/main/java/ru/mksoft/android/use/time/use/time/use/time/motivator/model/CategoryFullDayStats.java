package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.05.2022
 */
public class CategoryFullDayStats {
    private List<AppWithStats> appStats;
    private long totalTimeUsed;

}
