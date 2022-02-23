package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@DatabaseTable(tableName = "RULES")
public class Rule {
    @SuppressWarnings("JavaDoc")
    public static final String FIELD_RULE_NAME = "RULE_NAME";
//    public static final String DEFAULT_RULE_NAME = "GHDF-HGFH-TUYT-ASDF";
    private static final String DEFAULT_LIMIT_TIME_STRING = "00:00";

    @DatabaseField(generatedId = true)
    @Getter
    private Long id;

    @DatabaseField(columnName = FIELD_RULE_NAME, unique = true, width = 32, index = true, canBeNull = false)
    @Getter
    @Setter
    private String name;

    @DatabaseField(columnName = "MONDAY_MINUTES", canBeNull = false)
    private Integer mondayMinutes;

    @DatabaseField(columnName = "TUESDAY_MINUTES", canBeNull = false)
    private Integer tuesdayMinutes;

    @DatabaseField(columnName = "WEDNESDAY_MINUTES", canBeNull = false)
    private Integer wednesdayMinutes;

    @DatabaseField(columnName = "THURSDAY_MINUTES", canBeNull = false)
    private Integer thursdayMinutes;

    @DatabaseField(columnName = "FRIDAY_MINUTES", canBeNull = false)
    private Integer fridayMinutes;

    @DatabaseField(columnName = "SATURDAY_MINUTES", canBeNull = false)
    private Integer saturdayMinutes;

    @DatabaseField(columnName = "SUNDAY_MINUTES", canBeNull = false)
    private Integer sundayMinutes;

    public Integer[] getDays() {
        return new Integer[]{mondayMinutes, tuesdayMinutes, wednesdayMinutes, thursdayMinutes, fridayMinutes, saturdayMinutes, sundayMinutes};
    }

    public void setDays(int[] days) {
        mondayMinutes = days[0];
        tuesdayMinutes = days[1];
        wednesdayMinutes = days[2];
        thursdayMinutes = days[3];
        fridayMinutes = days[4];
        saturdayMinutes = days[5];
        sundayMinutes = days[6];
    }

    /**
     * Возвращает лимит времени за определённый день недели.
     *
     * @param dayOfWeek день недели
     * @return строка вида "HH:mm"
     */
    public String getHoursMinutesLimitTime(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return DEFAULT_LIMIT_TIME_STRING;
        }

        Integer time = 0;
        switch (dayOfWeek) {
            case MONDAY:
                time = mondayMinutes;
                break;
            case TUESDAY:
                time = tuesdayMinutes;
                break;
            case WEDNESDAY:
                time = wednesdayMinutes;
                break;
            case THURSDAY:
                time = thursdayMinutes;
                break;
            case FRIDAY:
                time = fridayMinutes;
                break;
            case SATURDAY:
                time = saturdayMinutes;
                break;
            case SUNDAY:
                time = sundayMinutes;
                break;
        }

        return getFormattedMinutesTime(time);
    }

    /**
     * Форматирует время.
     *
     * @param time время в минутах
     * @return строка вида "HH:mm"
     */
    public static String getFormattedMinutesTime(Integer time) {
        if (time == null) {
            return DEFAULT_LIMIT_TIME_STRING;
        }

        return String.format(Locale.ENGLISH, "%02d:%02d", time / 60, time % 60);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mondayMinutes=" + mondayMinutes +
                ", tuesdayMinutes=" + tuesdayMinutes +
                ", wednesdayMinutes=" + wednesdayMinutes +
                ", thursdayMinutes=" + thursdayMinutes +
                ", fridayMinutes=" + fridayMinutes +
                ", saturdayMinutes=" + saturdayMinutes +
                ", sundayMinutes=" + sundayMinutes +
                '}';
    }

    /**
     * Дни недели
     */
    public enum DayOfWeek {
        /**
         * Понедельник
         */
        MONDAY(0),
        /**
         * Вторник
         */
        TUESDAY(1),
        /**
         * Среда
         */
        WEDNESDAY(2),
        /**
         * Четверг
         */
        THURSDAY(3),
        /**
         * Пятница
         */
        FRIDAY(4),
        /**
         * Суббота
         */
        SATURDAY(5),
        /**
         * Воскресенье
         */
        SUNDAY(6);

        private final int dayNumber;

        DayOfWeek(int dayNumber) {
            this.dayNumber = dayNumber;
        }

        public int dayNumber() {
            return dayNumber;
        }
    }
}
