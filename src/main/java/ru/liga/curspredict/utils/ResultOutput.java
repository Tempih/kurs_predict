package ru.liga.curspredict.utils;

import java.util.List;

public class ResultOutput {
    /**
     * giveResult - выводит резултат расчетов в терминал
     *
     * @param resultList - спиок результатов для вывода
     */
    public static void giveResult(List<String> resultList) {// todo почему static?
        for (int i = 0; i < resultList.size(); i++) {
            WorkWithTerminal.textOutput(resultList.get(i));
        }
    }
}
