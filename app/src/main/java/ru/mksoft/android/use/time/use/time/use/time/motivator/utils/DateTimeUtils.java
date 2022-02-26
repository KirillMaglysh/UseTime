package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

import android.widget.TextView;
import androidx.annotation.NonNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.Rule;

import java.util.Locale;

/**
 * Утилиты для работы с датой и временем.
 *
 * @author Kirill
 * @since 26.02.2022
 */
public class DateTimeUtils {
    private static final String DAY_TIME_LIMIT_FORMAT = "%02d:%02d";

    /**
     * Возвращает отформатированное значение времени в минутах.
     *
     * @param time время в минутах
     * @return строка вида "HH:mm"
     */
    public static String getFormattedMinutesTime(int time) {
        return getFormattedHoursMinutesTime(time / 60, time % 60);
    }

    /**
     * Возвращает отформатированное значение времени.
     *
     * @param hours   время в часах
     * @param minutes время в минутах
     * @return строка вида "HH:mm"
     */
    public static String getFormattedHoursMinutesTime(int hours, int minutes) {
        return String.format(Locale.ENGLISH, DAY_TIME_LIMIT_FORMAT, hours, minutes);
    }

    /**
     * Возвращает отформатированный лимит времени правила за определённый день недели.
     *
     * @param rule      правило
     * @param dayOfWeek день недели
     * @return строка вида "HH:mm"
     */
    public static String getFormattedLimitTime(@NonNull Rule rule, @NonNull Rule.DayOfWeek dayOfWeek) {
        return getFormattedMinutesTime(rule.getTime(dayOfWeek));
    }

    /**
     * Распарсивает строковое значение времени (строка вида "HH:mm").
     *
     * @param time строковое значение времени
     * @return количество минут
     */
    public static int parseHoursMinutesTime(@NonNull CharSequence time) {
        int hours = Integer.parseInt(String.valueOf(time.subSequence(0, 2)));
        int minutes = Integer.parseInt(String.valueOf(time.subSequence(3, 5)));
        return hours * 60 + minutes;
    }

    /**
     * Распарсивает значение поля ввода времени (строка вида "HH:mm").
     *
     * @param timeField поля ввода времени
     * @return количество минут
     */
    public static int parseTimeFieldValue(@NonNull TextView timeField) {
        return parseHoursMinutesTime(timeField.getText());
    }
}
