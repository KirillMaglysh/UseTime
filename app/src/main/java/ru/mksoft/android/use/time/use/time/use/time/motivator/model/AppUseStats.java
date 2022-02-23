package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.types.SqlDateType;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */

@DatabaseTable(tableName = "APP_USE_STATS")
@Getter
@Setter
public class AppUseStats {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "USER_APP",
            foreign = true,
            foreignAutoRefresh = true,
            foreignAutoCreate = true,
            uniqueIndexName = "unique_app_date_idx",
            canBeNull = false)
    private UserApp userApp;

    @DatabaseField(columnName = "DATE", uniqueIndexName = "unique_app_date_idx", canBeNull = false)
    private SqlDateType date;

    @DatabaseField(columnName = "USAGE_TIME", canBeNull = false)
    private Long usageTime;
}
