package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.Map;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@DatabaseTable(tableName = "RULES")
public class Rule {
    @SuppressWarnings("JavaDoc")
    public static final String DAY_TIME_LIMIT_FORMAT = "%02d:%02d";
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

    /**
     * Задаёт лимиты времени
     *
     * @param timeLimits лимиты времени по дням недели
     */
    public void setDays(Map<DayOfWeek, Integer> timeLimits) {
        mondayMinutes = timeLimits.get(DayOfWeek.MONDAY);
        tuesdayMinutes = timeLimits.get(DayOfWeek.TUESDAY);
        wednesdayMinutes = timeLimits.get(DayOfWeek.WEDNESDAY);
        thursdayMinutes = timeLimits.get(DayOfWeek.THURSDAY);
        fridayMinutes = timeLimits.get(DayOfWeek.FRIDAY);
        saturdayMinutes = timeLimits.get(DayOfWeek.SATURDAY);
        sundayMinutes = timeLimits.get(DayOfWeek.SUNDAY);
    }

    /**
     * Возвращает количество часов лимита времени за определённый день недели.
     *
     * @param dayOfWeek день недели
     * @return количество часов лимита
     */
    public int getHours(DayOfWeek dayOfWeek) {
        Integer time = getTime(dayOfWeek);
        return time == null ? 0 : time / 60;
    }

    /**
     * Возвращает количество минут лимита времени за определённый день недели.
     *
     * @param dayOfWeek день недели
     * @return количество минут лимита
     */
    public int getMinutes(DayOfWeek dayOfWeek) {
        Integer time = getTime(dayOfWeek);
        return time == null ? 0 : time % 60;
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

        return getFormattedMinutesTime(getTime(dayOfWeek));
    }

    private Integer getTime(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return mondayMinutes;
            case TUESDAY:
                return tuesdayMinutes;
            case WEDNESDAY:
                return wednesdayMinutes;
            case THURSDAY:
                return thursdayMinutes;
            case FRIDAY:
                return fridayMinutes;
            case SATURDAY:
                return saturdayMinutes;
            case SUNDAY:
                return sundayMinutes;
            default:
                return null;
        }
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

        return String.format(Locale.ENGLISH, DAY_TIME_LIMIT_FORMAT, time / 60, time % 60);
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
