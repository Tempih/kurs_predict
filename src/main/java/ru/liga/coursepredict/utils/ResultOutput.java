package ru.liga.coursepredict.utils;

import java.util.List;

public class ResultOutput {
    /**
     * giveResult - выводит резултат расчетов в терминал
     *
     * @param resultList - спиок результатов для вывода
     */
    public void giveResult(List<String> resultList, WorkWithTerminal workWithTerminal) {
        for (String s : resultList) {
            workWithTerminal.textOutput(s);
        }
    }
}
