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
@DatabaseTable(tableName = "CATEGORIES")
public class Category {
    public static final String FIELD_CATEGORY_NAME = "CATEGORY_NAME";

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = FIELD_CATEGORY_NAME, unique = true, width = 32, index = true, canBeNull = false)
    private String name;
}
