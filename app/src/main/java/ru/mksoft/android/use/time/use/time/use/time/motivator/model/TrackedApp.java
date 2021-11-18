package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */

@Getter
@Setter
@DatabaseTable(tableName = "TRACKED_APP")
public class TrackedApp {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "APP_NAME", width = 32, index = true, canBeNull = false)
    private String name;

    @DatabaseField(columnName = "SYS_ID", width = 512, canBeNull = false)
    private String systemId;

    @DatabaseField(columnName = "CATEGORY", foreign = true, index = true, canBeNull = false)
    private Category category;
}
