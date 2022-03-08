package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.03.2022
 */
@DatabaseTable(tableName = "PROPERTY")
@Getter
@Setter
public class Property {
    public static final Long STRIKE_FIELD_ID = 1L;
    public static final Long USER_LEVEL_FIELD_ID = 2L;
    public static final Integer MAX_STRIKE = 7;
    //TODO() определить максимальный уровень
    public static final Integer MAX_LEVEL = 10;

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(columnName = "VALUE", canBeNull = false)
    private Integer value;
}
