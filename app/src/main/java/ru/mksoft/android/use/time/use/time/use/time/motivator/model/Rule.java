package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import androidx.annotation.NonNull;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.InvalidDayOfWeekException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao.InvalidTimeLimitException;

import java.util.Map;

/**
 * Class for database table "RULE" representation.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@DatabaseTable(tableName = "RULES")
public class Rule {
    @SuppressWarnings("JavaDoc")
    public static final String FIELD_RULE_NAME = "RULE_NAME";
    //    public static final String DEFAULT_RULE_NAME = "GHDF-HGFH-TUYT-ASDF";
    /**
     * Неограниченное время (максимальное количество минут в сутках).
     */
    public static final int NO_LIMIT_TIME = 23 * 60 + 59;

    @DatabaseField(generatedId = true)
    @Getter
    @Setter
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
     * Конструктор
     */
    public Rule() {
        setDailySameLimits(NO_LIMIT_TIME);
    }

    /**
     * Задаёт лимиты времени
     *
     * @param timeLimits лимиты времени по дням недели
     */
    public void setDayLimits(Map<DayOfWeek, Integer> timeLimits) {
        mondayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.MONDAY);
        tuesdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.TUESDAY);
        wednesdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.WEDNESDAY);
        thursdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.THURSDAY);
        fridayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.FRIDAY);
        saturdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.SATURDAY);
        sundayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.SUNDAY);
    }

    /**
     * Задаёт одинаковые лимиты времени для каждого дня
     *
     * @param timeLimit лимиты времени для всех дней недели
     */
    public void setDailySameLimits(@NonNull Integer timeLimit) {
        mondayMinutes = validateTimeLimitInMinutes(timeLimit);
        tuesdayMinutes = validateTimeLimitInMinutes(timeLimit);
        wednesdayMinutes = validateTimeLimitInMinutes(timeLimit);
        thursdayMinutes = validateTimeLimitInMinutes(timeLimit);
        fridayMinutes = validateTimeLimitInMinutes(timeLimit);
        saturdayMinutes = validateTimeLimitInMinutes(timeLimit);
        sundayMinutes = validateTimeLimitInMinutes(timeLimit);
    }

    /**
     * Возвращает количество часов лимита времени за определённый день недели.
     *
     * @param dayOfWeek день недели
     * @return количество часов лимита
     */
    public int getHoursLimitTime(@NonNull DayOfWeek dayOfWeek) {
        return getTime(dayOfWeek) / 60;
    }

    /**
     * Возвращает количество минут лимита времени за определённый день недели.
     *
     * @param dayOfWeek день недели
     * @return количество минут лимита
     */
    public int getMinutesLimitTime(@NonNull DayOfWeek dayOfWeek) {
        return getTime(dayOfWeek) % 60;
    }

    /**
     * Returns time limit за определённый день недели.
     *
     * @param dayOfWeek day of week
     * @return time limit
     */
    @NonNull
    public Integer getTime(@NonNull DayOfWeek dayOfWeek) {
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
                throw new InvalidDayOfWeekException();
        }
    }

    @NonNull
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

    private static Integer validateTimeLimitInMinutes(Map<DayOfWeek, Integer> timeLimits, DayOfWeek dayOfWeek) {
        return validateTimeLimitInMinutes(timeLimits.get(dayOfWeek));
    }

    private static Integer validateTimeLimitInMinutes(Integer minutes) {
        if (minutes == null || minutes.compareTo(0) < 0 || minutes.compareTo(NO_LIMIT_TIME) > 0) {
            throw new InvalidTimeLimitException(minutes);
        }

        return minutes;
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

        /**
         * Возвращает номер дня недели.
         * Неделя начинается с понедельника. Номер 0 !!!!!
         *
         * @return номер дня недели
         */
        public int dayNumber() {
            return dayNumber;
        }
    }
}
