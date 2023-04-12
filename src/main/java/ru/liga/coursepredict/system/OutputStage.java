package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.enums.Output;
import ru.liga.coursepredict.exceptions.IncorrectDateFormatException;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.graph.CreateGraph;
import ru.liga.coursepredict.model.PredictResult;
import ru.liga.coursepredict.outputcreater.ResultOutput;

import java.io.IOException;
import java.util.List;

import static ru.liga.coursepredict.constants.Constants.EMPTY_STRING;
import static ru.liga.coursepredict.graph.SaveToFile.saveToFile;
@Slf4j
public class OutputStage {
    private static final CreateGraph createGraph = new CreateGraph();
    private static final ResultOutput resultOutput = new ResultOutput();
    private static final Formatter formatter = new Formatter();

    public String startOutputResult(List<PredictResult> predictResult, String paramOutput){

        String outputString = EMPTY_STRING;
        Output output = Output.valueOf(paramOutput.toUpperCase());
        log.debug("Начался выбор вида вывода результата");
        switch (output) {
            case GRAPH -> {
                try {
                    log.debug("Начался процесс формирования графика");
                    outputString = saveToFile(createGraph.createGraph(predictResult));
                } catch (IOException | IncorrectDateFormatException e) {
                    log.debug("Ошибка в работе программы");
                }
                log.debug("Процесс формирования графика закончился");
            }
            case LIST -> {
                log.debug("Начался процесс формирования списка");
                for (PredictResult result : predictResult) {
                    List<String> resultList = formatter.startFormatResult(result);
                    if (!resultList.isEmpty()) {
                        outputString = outputString.concat(resultOutput.giveResultForTg(result.getCurrency(), resultList));
                    } else {
                        log.debug("Полученный список оказался пустой");
                        return outputString;
                    }
                }
                log.debug("Процесс формирования списка закончился");
            }
        }
        log.info("Был выбран вывод в виде {}", paramOutput);
        return outputString;
    }
}
