package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

/**
 * Ошибка базы данных.
 *
 * @author Kirill
 * @since 26.02.2022
 */
public class DatabaseException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Database error";

    /**
     * Конструктор.
     *
     * @param cause причина ошибки
     */
    public DatabaseException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
