package ru.liga.coursepredict.calculations;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.model.CourseTable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AvgSumPredict {
    private static final BigDecimal ZERO_DIVIDE = new BigDecimal(0);
    private static final Integer COUNT_DAYS_FOR_AVG_CALCULATION = 7;
    private static final Integer INDEX_FOR_ADD_NEW_COURSE = 0;

    /**
     * avgSumArray производит расчет среднего арифметического для входного списка
     *
     * @param array - дата
     * @return среднее арифметическое
     */
    public BigDecimal avgSumArray(List<BigDecimal> array) {
        BigDecimal divider = new BigDecimal(array.size());
        if (divider.equals(ZERO_DIVIDE)) {
            log.debug("Произошло деление на 0");
            return null;
        }
        return array.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(divider, MathContext.DECIMAL128);
    }

    /**
     * convertDate производит расчет курса валют на неделю
     *
     * @param currencyTable - ArrayList значений курса валют
     * @return список предсказанных курсов валют
     */
    public List<BigDecimal> predict(List<CourseTable> currencyTable, Integer countDay) {
        log.debug("Начинаем расчет курса валют");
        List<BigDecimal> lastCourses = currencyTable.stream()
                .limit(COUNT_DAYS_FOR_AVG_CALCULATION)
                .map(values -> values.getCurs().divide(new BigDecimal(values.getNominal()), MathContext.DECIMAL128))
                .collect(Collectors.toList());
        List<BigDecimal> predictedCourses = new ArrayList<>();
        if (lastCourses.size() < COUNT_DAYS_FOR_AVG_CALCULATION) {
            log.debug("Ошибка в получении последних {} дней",COUNT_DAYS_FOR_AVG_CALCULATION);
            return lastCourses;
        }
        for (int i = 0; i < countDay; i++) {
            BigDecimal newCurs = avgSumArray(lastCourses);

            if (newCurs == null) {
                return lastCourses;
            }
            lastCourses.add(INDEX_FOR_ADD_NEW_COURSE, newCurs);
            lastCourses.remove(lastCourses.size() - 1);
            predictedCourses.add(predictedCourses.size(), newCurs);
        }
        log.debug("Закончили расчет курса валют");
        return predictedCourses;
    }

}


