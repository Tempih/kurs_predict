package ru.liga.curspredict.utils;

import ru.liga.curspredict.structure.CursTable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calculations {
    private static BigDecimal zeroDivide = new BigDecimal(0);

    /**
     * avgSumOfrray производит расчет среднего арефметического для ArrayList
     *
     * @param array - дата
     */
    public static BigDecimal avgSumArray(List<BigDecimal> array) {
        BigDecimal sum;
        BigDecimal divider = new BigDecimal(array.size());
        if (divider.equals(zeroDivide)){
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
    public static List<BigDecimal> lastCurses(List<CursTable> currencyTable, Integer countCurses) {
        List<BigDecimal> curses = new ArrayList<>();
        BigDecimal curs, nominal, divideResult;
        for (int i = 0; i < countCurses; i++) {
            curs = currencyTable.get(i).getCurs();
            nominal = new BigDecimal(currencyTable.get(i).getNominal());
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
    public static List<BigDecimal> predict(List<CursTable> currencyTable, Integer countDay) {
        List<BigDecimal> lastCurses = lastCurses(currencyTable, 7);

        for (int i = 0; i < countDay; i++) {
            BigDecimal newCurs = Calculations.avgSumArray(lastCurses);
            if (newCurs == null) {
                return null;
            }
            lastCurses.add(0, newCurs);
            lastCurses.remove(lastCurses.size() - 1);
        }
        while (lastCurses.size() != countDay) {
            lastCurses.remove(lastCurses.size() - 1);
        }
        Collections.reverse(lastCurses);
        return lastCurses;
    }

}
