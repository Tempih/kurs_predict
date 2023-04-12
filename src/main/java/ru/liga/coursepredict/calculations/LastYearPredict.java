package ru.liga.coursepredict.calculations;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.model.CourseTable;
import ru.liga.coursepredict.model.PredictMoonMist;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.liga.coursepredict.constants.Constants.FORMATTER;

@Slf4j
public class LastYearPredict {
    private static final Integer ONE = 1;

    public List<BigDecimal> predict(List<CourseTable> currencyTable, List<String> subYearDateList) {
        int countDays = subYearDateList.size();
        log.debug("Начинаем расчет курса валют");
        List<PredictMoonMist> dateFilteredCurrencyList = currencyTable.stream()
                .filter(courseTable -> subYearDateList.contains(courseTable.getDate()))
                .map(courseTable -> new PredictMoonMist(courseTable.getCurs(), courseTable.getDate()))
                .collect(toList());

        if (dateFilteredCurrencyList.size() != countDays) {
            log.debug("Не все даты были в прошлом, начинаем перебор прошлых дней");
            for (PredictMoonMist predictMoonMist : dateFilteredCurrencyList) {
                subYearDateList.remove(predictMoonMist.getDate());
            }
            log.debug("Количество отсутствующих дат {}", subYearDateList.size());
            List<String> notIncludedDatesMinusDate = subYearDateList;
            log.debug("Входим в цикл while(true)");
            while (true) {
                notIncludedDatesMinusDate = notIncludedDatesMinusDate.stream()
                        .map(date -> LocalDate.parse(date, FORMATTER).minusDays(ONE).format(FORMATTER))
                        .collect(Collectors.toList());
                log.debug("Вычли один день из дат и получили {}", notIncludedDatesMinusDate);

                List<String> finalNotIncludedDatesMinusDate = notIncludedDatesMinusDate;
                List<PredictMoonMist> newCursAndDatesForAdd = currencyTable.stream()
                        .filter(courseTable -> finalNotIncludedDatesMinusDate.contains(courseTable.getDate()))
                        .map(courseTable -> new PredictMoonMist(courseTable.getCurs(), courseTable.getDate()))
                        .toList();

                log.debug("Получили таки новые даты и валюты:{}", newCursAndDatesForAdd);
                dateFilteredCurrencyList.addAll(newCursAndDatesForAdd);
                if (dateFilteredCurrencyList.size() == countDays) {
                    log.debug("Вышли из цикла while(true)");
                    break;
                }
                for (PredictMoonMist predictMoonMist : newCursAndDatesForAdd) {
                    notIncludedDatesMinusDate.remove(predictMoonMist.getDate());
                }
                log.debug("Новая итерация цикла while(true)");

            }
        }
        log.debug("Закончили расчет курса валют");

        return dateFilteredCurrencyList.stream()
                .sorted(Comparator.comparing(PredictMoonMist::getDate))
                .map(PredictMoonMist::getCourse).toList();
    }
}
