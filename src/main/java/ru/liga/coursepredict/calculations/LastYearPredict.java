package ru.liga.coursepredict.calculations;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.structure.CourseTable;
import ru.liga.coursepredict.structure.PredictMoonMist;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
public class LastYearPredict {
    private static final Integer ONE = 1;

    public List<BigDecimal> predict(List<CourseTable> currencyTable, List<String> subYearDateList, Formatter formatter) {
        log.debug("Начинаем расчет курса валют");
        List<PredictMoonMist> dateFilteredCurrencyList = currencyTable.stream()
                .filter(courseTable -> subYearDateList.contains(courseTable.getDate()))
                .map(courseTable -> new PredictMoonMist(courseTable.getCurs(), courseTable.getDate()))
                .collect(toList());

        if (dateFilteredCurrencyList.size() != subYearDateList.size()) {
            log.debug("Не все даты были в прошлом, начинаем перебор прошлых дней");

            List<String> notIncludedDates = subYearDateList.stream()
                    .filter(key -> !dateFilteredCurrencyList.stream().map(PredictMoonMist::getDate).toList().contains(key))
                    .toList();
            log.debug("Количество отсутствующих дат".concat(Integer.toString(notIncludedDates.size())));

            int daysAgo = ONE;
            while (true) {
                List<String> notIncludedDatesMinusDate = formatter.subDaysFromDate(notIncludedDates, daysAgo);

                currencyTable.stream()
                        .filter(courseTable -> notIncludedDatesMinusDate.contains(courseTable.getDate()))
                        .map(courseTable -> new PredictMoonMist(courseTable.getCurs(), courseTable.getDate()))
                        .forEachOrdered(dateFilteredCurrencyList::add);
                if (dateFilteredCurrencyList.size() == subYearDateList.size()) {
                    break;
                }
                notIncludedDates = notIncludedDatesMinusDate.stream()
                        .filter(key -> !dateFilteredCurrencyList.stream().map(PredictMoonMist::getDate).toList().contains(key))
                        .toList();
            }
        }
        dateFilteredCurrencyList.sort(Comparator.comparing(PredictMoonMist::getDate));
        log.debug("Закончили расчет курса валют");

        return dateFilteredCurrencyList.stream().map(PredictMoonMist::getCourse).toList();
    }
}
