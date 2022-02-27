package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

import java.util.Locale;

/**
 * Некорректное значение времени.
 *
 * @author Kirill
 * @since 25.02.2022
 */
public class InvalidTimeLimitException extends IllegalArgumentException {
    private static final String ERROR_MESSAGE = "Incorrect time limit value: %02d:%02d";
    private static final String ERROR_MESSAGE_MINUTES = "Incorrect time limit value: %d minutes";

    /**
     * Конструктор.
     *
     * @param hours   количество часов
     * @param minutes количество минут
     */
    public InvalidTimeLimitException(Integer hours, Integer minutes) {
        super(String.format(Locale.US, ERROR_MESSAGE, hours, minutes));
    }

    /**
     * Конструктор.
     *
     * @param minutes лимит времени в минутах
     */
    public InvalidTimeLimitException(Integer minutes) {
        super(String.format(Locale.US, ERROR_MESSAGE_MINUTES, minutes));
    }
}
