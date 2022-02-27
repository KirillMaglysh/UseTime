package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    public static final String FIELD_USER_APP = "USER_APP";
    public static final String FIELD_DATE = "DATE";

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "USER_APP",
            foreign = true,
            foreignAutoRefresh = true,
            foreignAutoCreate = true,
            uniqueIndexName = "unique_app_date_idx",
            canBeNull = false)
    private UserApp userApp;

    @DatabaseField(columnName = "DATE", dataType = DataType.DATE_STRING, format = "yyyy-MM-dd", uniqueIndexName = "unique_app_date_idx", canBeNull = false)
    private Date date;

    @DatabaseField(columnName = "USAGE_TIME", canBeNull = false)
    private Long usageTime;
}
