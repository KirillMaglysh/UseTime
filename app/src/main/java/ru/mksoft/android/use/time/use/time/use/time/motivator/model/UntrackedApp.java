package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 07.01.2022
 */

@Getter
@Setter
@DatabaseTable(tableName = "TRACKED_APP")
public class UntrackedApp {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "SYS_ID", width = 512, canBeNull = false)
    private Integer systemId;

    @DatabaseField(columnName = "APP_NAME", width = 32, index = true, canBeNull = false)
    private String name;
}
