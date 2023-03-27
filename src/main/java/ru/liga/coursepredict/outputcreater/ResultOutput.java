package ru.liga.coursepredict.outputcreater;

import ru.liga.coursepredict.printer.WorkWithTerminal;

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
