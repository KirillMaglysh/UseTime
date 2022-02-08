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
@DatabaseTable(tableName = "USER_APP")
public class UserApp {
    public static final String FIELD_IS_TRACKED = "IS_TRACKED";

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "SYS_ID", width = 512, canBeNull = false)
    private Integer systemId;

    @DatabaseField(columnName = "PACKAGE_NAME", width = 32, index = true, canBeNull = false)
    private String packageName;

    @DatabaseField(columnName = "CATEGORY", foreign = true, index = true, canBeNull = false)
    private Category category;

    @DatabaseField(columnName = "IS_TRACKED", index = true, canBeNull = false)
    private Boolean isTracked = false;
}