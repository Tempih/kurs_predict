package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.enums.Period;
import ru.liga.coursepredict.telegram.Bot;

import static ru.liga.coursepredict.constants.Constants.ZERO;
import static ru.liga.coursepredict.outputcreater.InfoOutput.givePeriodError;
@Slf4j
public class PeriodSelectStage {
    private static final Integer COUNT_DAYS_IN_WEEK = 7;
    private static final Integer ONE_DAY = 1;
    private static final Integer COUNT_DAYS_IN_MONTH = 30;
    private static final Bot bot = new Bot();

    public Integer selectPeriod(String inputPeriod, Long chatId) {
        Integer countDays = Integer.parseInt(ZERO);

        try {
            Period period = Period.valueOf(inputPeriod.toUpperCase());
            log.debug("Введенный период есть в списке");
            switch (period) {
                case WEEK -> countDays = COUNT_DAYS_IN_WEEK;
                case TOMORROW -> countDays = ONE_DAY;
                case MONTH -> countDays = COUNT_DAYS_IN_MONTH;
            }

        } catch (IllegalArgumentException e) {
            log.debug("Введенный период отсутствует в списке");
            bot.sendText(chatId, givePeriodError(Period.values()));
            return countDays;
        }
        log.info("Для периода {} получено {} дней", inputPeriod, countDays);
        return countDays;
    }
}
