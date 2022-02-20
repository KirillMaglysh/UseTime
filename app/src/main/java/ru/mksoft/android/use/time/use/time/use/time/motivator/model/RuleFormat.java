package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditRuleBinding;

import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 12.02.2022
 */
public class RuleFormat {
    public static int[] parseRuleInput(FragmentEditRuleBinding binding) {
        return new int[]{
                Integer.parseInt(binding.mondayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.mondayMinutes.getText().toString()),
                Integer.parseInt(binding.tuesdayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.tuesdayMinutes.getText().toString()),
                Integer.parseInt(binding.wednesdayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.wednesdayMinutes.getText().toString()),
                Integer.parseInt(binding.thursdayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.thursdayMinutes.getText().toString()),
                Integer.parseInt(binding.fridayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.fridayMinutes.getText().toString()),
                Integer.parseInt(binding.saturdayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.saturdayMinutes.getText().toString()),
                Integer.parseInt(binding.sundayHours.getText().toString()) * 60
                        + Integer.parseInt(binding.sundayMinutes.getText().toString()),
        };
    }

    public static class ShortHourMinuteFormat {
        private String minuteString;
        private String hourString;

        public ShortHourMinuteFormat(Rule rule) {
            Integer[] days = rule.getDays();
            minuteString = String.format(Locale.ENGLISH, "М %1$02d %2$02d %3$02d %4$02d %5$02d %6$02d %7$02d",
                    days[0] % 60, days[1] % 60, days[2] % 60, days[3] % 60, days[4] % 60, days[5] % 60, days[6] % 60);

            hourString = String.format(Locale.ENGLISH, "Ч %1$02d %2$02d %3$02d %4$02d %5$02d %6$02d %7$02d",
                    days[0] / 60, days[1] / 60, days[2] / 60, days[3] / 60, days[4] / 60, days[5] / 60, days[6] / 60);
        }

        public String getHourString() {
            return hourString;
        }

        public String getMinuteString() {
            return minuteString;
        }

    }
}
