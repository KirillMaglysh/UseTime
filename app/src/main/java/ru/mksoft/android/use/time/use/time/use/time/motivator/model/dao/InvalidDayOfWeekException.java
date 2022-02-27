package ru.mksoft.android.use.time.use.time.use.time.motivator.model.dao;

/**
 * День недели не может быть пустым.
 *
 * @author Kirill
 * @since 25.02.2022
 */
public class InvalidDayOfWeekException extends NullPointerException {
    private static final String ERROR_MESSAGE = "Day of the week cannot be null";

    /**
     * Конструктор.
     */
    public InvalidDayOfWeekException() {
        super(ERROR_MESSAGE);
    }
}
