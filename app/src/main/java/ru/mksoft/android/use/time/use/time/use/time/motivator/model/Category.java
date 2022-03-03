package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for database table "CATEGORIES" representation.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@Getter
@Setter
@DatabaseTable(tableName = "CATEGORIES")
public class Category {
    /**
     * CATEGORY_NAME field name
     */
    public static final String FIELD_CATEGORY_NAME = "CATEGORY_NAME";

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(generatedId = true)
    private Long id;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = "RULE_ID",
            foreign = true,
            foreignAutoRefresh = true,
            columnDefinition = "integer references RULES(id) on delete restrict",
            index = true,
            canBeNull = false)
    private Rule rule;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = FIELD_CATEGORY_NAME,
            unique = true,
            width = 32,
            index = true,
            canBeNull = false)
    private String name;
}
