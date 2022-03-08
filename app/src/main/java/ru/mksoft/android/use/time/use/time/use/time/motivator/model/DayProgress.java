package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.03.2022
 */
@AllArgsConstructor
@Getter
public class DayProgress {
    private int failedGoalNumber;
    private long timeUsed;

    public void plusTimeUsed(long add) {
        timeUsed += add;
    }

    public void plusFailedGoal(long add) {
        failedGoalNumber += add;
    }
}
