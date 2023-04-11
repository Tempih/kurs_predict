package ru.liga.coursepredict.outputcreater;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static ru.liga.coursepredict.constants.Constants.LINE_BREAKER;

@Slf4j
public class ResultOutput {
    public String giveResultForTg(String currency, List<String> resultList) {
        log.debug("Начинаем готовить список для тг");
        String outputString = currency.toUpperCase().concat(LINE_BREAKER);
        for (String s : resultList) {
            outputString = outputString.concat(s).concat(LINE_BREAKER);
        }
        log.debug("Закончили готовить список для тг");
        return outputString;
    }
}
