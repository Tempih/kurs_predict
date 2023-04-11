package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.calculations.AvgSumPredict;
import ru.liga.coursepredict.calculations.LastYearPredict;
import ru.liga.coursepredict.calculations.LinearRegression;
import ru.liga.coursepredict.enums.PredictAlgorithms;
import ru.liga.coursepredict.enums.PredictPeriod;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.model.CourseTable;
import ru.liga.coursepredict.model.PredictResult;
import ru.liga.coursepredict.parser.Parser;
import ru.liga.coursepredict.telegram.Bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static ru.liga.coursepredict.outputcreater.InfoOutput.giveCalculationError;
import static ru.liga.coursepredict.constants.Constants.*;

@Slf4j

public class AlgorithmSelectStage {
    private static final Integer LAST_DATE_INDEX = 0;
    private static final Integer ONE_DAY = 1;
    private static final AvgSumPredict avgSumPredict = new AvgSumPredict();
    private static final LastYearPredict lastYearPredict = new LastYearPredict();
    private static final PeriodSelectStage selectPeriod = new PeriodSelectStage();
    private static final Bot bot = new Bot();
    private static final Formatter formatter = new Formatter();
    private static final Parser parser = new Parser();
    public List<PredictResult> startPredict(Map<String, List<CourseTable>> currencyTables, String inputPredictPeriod, String inputParamPeriod, String predictAlgorithm, Long chatId) {
        PredictPeriod predictPeriod = PredictPeriod.valueOf(inputPredictPeriod.toUpperCase());
        Integer countDays = Integer.parseInt(ZERO);
        List<BigDecimal> predictResult;
        List<String> dateList = new ArrayList<>();
        String lastDate = "";
        List<PredictResult> predictResultList = new ArrayList<>();
        log.debug("Начался процесс прогнозирования");
        for (Map.Entry<String, List<CourseTable>> item : currencyTables.entrySet()) {
            log.debug("Начался выбор периода/даты для прогнозирования");
            switch (predictPeriod) {
                case PERIOD -> {
                    lastDate = item.getValue().get(LAST_DATE_INDEX).getDate();
                    countDays = selectPeriod.selectPeriod(inputParamPeriod, chatId);
                    dateList = formatter.formatOutputDate(lastDate, countDays);
                }
                case DATE -> {
                    lastDate = inputParamPeriod;
                    countDays = ONE_DAY;
                    dateList.add(lastDate);
                }
            }
            if (countDays == Integer.parseInt(ZERO) || lastDate.equals(EMPTY_STRING)) {
                log.debug("Процесс выбора периода/даты закончился c ошибкой");
                return predictResultList;
            }
            log.debug("Процесс выбора периода/даты закончился");
            log.info("Получен {} на {} дней", inputPredictPeriod, countDays);
            PredictAlgorithms predictAlgorithms = PredictAlgorithms.valueOf(predictAlgorithm.toUpperCase());
            switch (predictAlgorithms) {
                case AVG -> {
                    log.debug("Алгоритм предсказания {} начался", predictAlgorithm);
                    predictResult = avgSumPredict.predict(item.getValue(), countDays);
                    if (predictResult.size() != countDays) {
                        log.debug("Количество дней не совпало с количеством полученных курс валюты");
                        bot.sendText(chatId,giveCalculationError());
                        return new ArrayList<>();
                    }
                    predictResultList.add(new PredictResult(item.getKey(), predictResult, dateList));
                    log.debug("Было добавлено {} строк курса валюты {}", predictResult.size(), item.getKey());
                    log.debug("Алгоритм предсказания {} закончился", predictAlgorithm);
                }
                case MOON -> {
                    log.debug("Алгоритм предсказания {} начался", predictAlgorithm);
                    List<String> dateListMinusOneYear = formatter.subYearFromDate(dateList);
                    log.debug("Год был вычтен из дат для предсказания");
                    predictResult = lastYearPredict.predict(item.getValue(), dateListMinusOneYear);
                    if (predictResult.size()!=countDays) {
                        log.debug("Количество дней не совпало с количеством полученных курс валюты");
                        bot.sendText(chatId,giveCalculationError());
                        return new ArrayList<>();
                    }
                    predictResultList.add(new PredictResult(item.getKey(), predictResult, dateList));
                    log.debug("Было добавлено {} строк курса валюты {}", predictResult.size(), item.getKey());
                    log.debug("Алгоритм предсказания {} закончился", predictAlgorithm);
                }
                case MIST -> {
                    log.debug("Алгоритм предсказания {} начался", predictAlgorithm);
                    Integer minYear = parser.getMinYear(item.getValue());
                    log.debug("Минимальный год: {}", minYear);
                    Integer maxYear = parser.getMaxYear(item.getValue());
                    log.debug("Максимальный год: {}", maxYear.toString());
                    List<String> dateListWithRandomYear = formatter.randomYearForDate(dateList, minYear, maxYear);
                    log.debug("Год был рандомно выбран из доступных для предсказания");
                    predictResult = lastYearPredict.predict(item.getValue(), dateListWithRandomYear);
                    if (predictResult.size() != countDays) {
                        log.debug("Количество дней не совпало с количеством полученных курс валюты");
                        bot.sendText(chatId,giveCalculationError());
                        return new ArrayList<>();
                    }
                    predictResultList.add(new PredictResult(item.getKey(), predictResult, dateList));
                    log.debug("Было добавлено {} строк курса валюты {}", predictResult.size(), item.getKey());
                    log.debug("Алгоритм предсказания {} закончился", predictAlgorithm);
                }
                case REG -> {
                    log.debug("Алгоритм предсказания {} начался", predictAlgorithm);
                    predictResult = new ArrayList<>();
                    BigDecimal lastDateFromCurrencyTable = formatter.convertDateToUnixTimeMinusMonth(item.getValue().get(LAST_DATE_INDEX).getDate());
                    log.debug("Последняя дата для предсказания были переведены в unix время");
                    Map<BigDecimal, BigDecimal> currencyDateMap = item.getValue().stream()
                            .collect(Collectors.toMap(courseTable -> formatter.convertDateToUnixTime(courseTable.getDate()),
                                    CourseTable::getCurs))
                            .entrySet()
                            .stream()
                            .filter(dateLists -> dateLists.getKey().compareTo(lastDateFromCurrencyTable) >= Integer.parseInt(ZERO))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    log.debug("Получен отфильтрованный словарь курса валют с unix датой за последний месяц");
                    List<BigDecimal> dateListForRegression = new ArrayList<>(currencyDateMap.keySet());
                    List<BigDecimal> currencyListForRegression = new ArrayList<>(currencyDateMap.values());
                    LinearRegression linearRegression = new LinearRegression(dateListForRegression, currencyListForRegression);
                    log.debug("Получено уравнение линейной регрессии");
                    log.debug("Начинается предсказание по полученному уравнению");
                    for (String s : dateList) {
                        predictResult.add(linearRegression.predict(formatter.convertDateToUnixTime(s)));
                    }
                    log.debug("Предсказание по полученному уравнению закончилось");
                    if (predictResult.size() != countDays) {
                        log.debug("Количество дней не совпало с количеством полученных курс валюты");
                        bot.sendText(chatId,giveCalculationError());
                        return new ArrayList<>();
                    }
                    predictResultList.add(new PredictResult(item.getKey(), predictResult, dateList));
                    log.debug("Было добавлено {} строк курса валюты {}", predictResult.size(), item.getKey());
                    log.debug("Алгоритм предсказания {} закончился", predictAlgorithm);
                }
            }
            log.info("Алгоритм предсказания {} для валюты {} выполнен успешно", predictAlgorithm, item.getKey());
        }
        return predictResultList;
    }
}
