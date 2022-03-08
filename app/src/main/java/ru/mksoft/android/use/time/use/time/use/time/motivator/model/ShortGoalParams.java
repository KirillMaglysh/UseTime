package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 07.03.2022
 */
@AllArgsConstructor
@Getter
public class ShortGoalParams {
    private int timeLimit;
    private int usedTime;

    public boolean isCompleted() {
        return timeLimit >= usedTime;
    }
}
