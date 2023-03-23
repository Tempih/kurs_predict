package ru.liga.coursepredict.utils;

import ru.liga.coursepredict.structure.CourseTable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Calculations {
    private static final BigDecimal ZERO_DIVIDE = new BigDecimal(0);
    private static final Integer COUNT_DAYS_FOR_PREDICT = 7;
    private static final Integer INDEX_FOR_ADD_NEW_COURSE = 0;

    /**
     * avgSumOfrray производит расчет среднего арефметического для ArrayList
     *
     * @param array - дата
     */
    public BigDecimal avgSumArray(List<BigDecimal> array) {
        BigDecimal sum;
        BigDecimal divider = new BigDecimal(array.size());
        if (divider.equals(ZERO_DIVIDE)) {
            return null;
        }
        sum = array.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(divider, MathContext.DECIMAL128);
    }


    /**
     * lastSevenCurs производит получение последних 7 расчетов курса валют
     *
     * @param currencyTable - ArrayList значений
     */
    public List<BigDecimal> lastCurses(List<CourseTable> currencyTable, Integer countCurses) {
        List<BigDecimal> curses = new ArrayList<>();
        BigDecimal curs, nominal, divideResult;
        for (int i = 0; i < countCurses; i++) {
            curs = currencyTable.get(i).getCurs();
            nominal = new BigDecimal(currencyTable.get(i).getNominal());
            if (nominal.equals(ZERO_DIVIDE)) {
                break;
            }
            divideResult = curs.divide(nominal, MathContext.DECIMAL128);
            curses.add(divideResult);
        }
        return curses;
    }


    /**
     * `
     * convertDate производит расчет курса валют на неделю
     *
     * @param currencyTable - ArrayList значений курса валют
     */
    public List<BigDecimal> predict(List<CourseTable> currencyTable, Integer countDay) {
        List<BigDecimal> lastCurses = lastCurses(currencyTable, COUNT_DAYS_FOR_PREDICT);
        if (lastCurses.size() < COUNT_DAYS_FOR_PREDICT) {
            return lastCurses;
        }

        for (int i = 0; i < countDay; i++) {
            BigDecimal newCurs = avgSumArray(lastCurses);
            if (newCurs == null) {
                return lastCurses;
            }
            lastCurses.add(INDEX_FOR_ADD_NEW_COURSE, newCurs);
            lastCurses.remove(lastCurses.size() - 1);
        }
        while (lastCurses.size() != countDay) {
            lastCurses.remove(lastCurses.size() - 1);
        }
        Collections.reverse(lastCurses);
        return lastCurses;
    }

}