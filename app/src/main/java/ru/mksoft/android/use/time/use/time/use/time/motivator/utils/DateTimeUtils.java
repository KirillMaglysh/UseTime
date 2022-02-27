package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

import android.widget.TextView;
import androidx.annotation.NonNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Utils for work with date and time.
 *
 * @author Kirill
 * @since 26.02.2022
 */
public class DateTimeUtils {
    private static final String DAY_TIME_LIMIT_FORMAT = "%02d:%02d";

    /**
     * Returns formatted time value in minutes
     *
     * @param time time in minutes
     * @return string in format "HH:mm"
     */
    public static String getFormattedMinutesTime(int time) {
        return getFormattedHoursMinutesTime(time / 60, time % 60);
    }

    /**
     * Returns formatted time value.
     *
     * @param hours   time in hours
     * @param minutes time in minutes
     * @return string in format "HH:mm"
     */
    public static String getFormattedHoursMinutesTime(int hours, int minutes) {
        return String.format(Locale.ENGLISH, DAY_TIME_LIMIT_FORMAT, hours, minutes);
    }

    /**
     * Returns formatted rule time limit for specific day of the week.
     *
     * @param rule      rule
     * @param dayOfWeek day of week
     * @return string in format "HH:mm"
     */
    public static String getFormattedLimitTime(@NonNull Rule rule, @NonNull Rule.DayOfWeek dayOfWeek) {
        return getFormattedMinutesTime(rule.getTime(dayOfWeek));
    }

    /**
     * Parse string time value (string in format "HH:mm").
     *
     * @param time time value in string format
     * @return minutes number
     */
    public static int parseHoursMinutesTime(@NonNull CharSequence time) {
        int hours = Integer.parseInt(String.valueOf(time.subSequence(0, 2)));
        int minutes = Integer.parseInt(String.valueOf(time.subSequence(3, 5)));
        return hours * 60 + minutes;
    }

    /**
     * Parse value of time input field (string in format "HH:mm").
     *
     * @param timeField time input field
     * @return minutes value
     */
    public static int parseTimeFieldValue(@NonNull TextView timeField) {
        return parseHoursMinutesTime(timeField.getText());
    }

    /**
     * Returns begin of current date
     *
     * @return current date begin
     */
    public static Date getDateOfCurrentDayBegin() {
        return Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
