package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.calculations.AvgSumPredict;
import ru.liga.coursepredict.calculations.LastYearPredict;
import ru.liga.coursepredict.calculations.LinearRegression;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.exceptions.IncorrectCurrencyException;
import ru.liga.coursepredict.exceptions.IncorrectInputException;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.graph.CreateGraph;
import ru.liga.coursepredict.parser.Parser;
import ru.liga.coursepredict.structure.*;
import ru.liga.coursepredict.outputcreater.*;
import ru.liga.coursepredict.telegram.Bot;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.liga.coursepredict.constants.Constants.*;
import static ru.liga.coursepredict.graph.SaveToFile.saveToFile;
import static ru.liga.coursepredict.outputcreater.InfoOutput.*;
import static ru.liga.coursepredict.outputcreater.InfoOutput.giveFormatError;
import static ru.liga.coursepredict.validation.ValidateInput.checkCorrectInput;

@Slf4j
public class StageControl {
    private static final String STANDARD_FIRST_WORD = "rate";
    private static final Parser parser = new Parser();
    private static final Formatter formatter = new Formatter();
    private static final AvgSumPredict avgSumPredict = new AvgSumPredict();
    private static final LastYearPredict lastYearPredict = new LastYearPredict();
    private static final ResultOutput resultOutput = new ResultOutput();
    private static final Bot bot = new Bot();
    private static final String DIRECTORY = "course_data";
    private static final String USD_FILE = "usd.csv";
    private static final String LEV_FILE = "lev.csv";
    private static final String DRAM_FILE = "dram.csv";
    private static final String EUR_FILE = "eur.csv";
    private static final String TRY_FILE = "try.csv";
    private static final Integer LAST_DATE_INDEX = 0;
    private static final Integer COUNT_DAYS_IN_WEEK = 7;
    private static final Integer ONE_DAY = 1;
    private static final Integer COUNT_DAYS_IN_MONTH = 30;
    private static final Integer INDEX_FIRST_INPUT_WORD = 0;
    private static final Integer INDEX_SECOND_INPUT_WORD = 1;
    private static final Integer INDEX_THIRD_INPUT_WORD = 2;
    private static final Integer INDEX_FORTH_INPUT_WORD = 3;
    private static final Integer INDEX_FIFTH_INPUT_WORD = 5;
    private static final Integer INDEX_SIXTH_INPUT_WORD = 6;
    private static final Integer INDEX_SEVENTH_INPUT_WORD = 7;
    private static final CreateGraph createGraph = new CreateGraph();
    private static final String DEFAULT_OUTPUT_PARAM = "list";

    /**
     * Класс selectCurrency производит выбор файла для загрузки данных по выбранной валюте
     *
     * @param currency - валюта
     */
    public List<CourseTable> selectCurrency(String currency, Long chatId) {
        List<CourseTable> currencyList = new ArrayList<>();

        try {
            Currency currencies = Currency.lookup(currency.toUpperCase());
            log.debug("Введенная валюта есть в списке");
            switch (currencies) {
                case USD -> currencyList = parser.getDataFromFile(DIRECTORY.concat(File.separator).concat(USD_FILE));
                case EUR -> currencyList = parser.getDataFromFile(DIRECTORY.concat(File.separator).concat(EUR_FILE));
                case TRY -> currencyList = parser.getDataFromFile(DIRECTORY.concat(File.separator).concat(TRY_FILE));
                case LEV -> currencyList = parser.getDataFromFile(DIRECTORY.concat(File.separator).concat(LEV_FILE));
                case DRAM -> currencyList = parser.getDataFromFile(DIRECTORY.concat(File.separator).concat(DRAM_FILE));
                default -> throw new IncorrectCurrencyException();
            }
        } catch (RuntimeException e) {
            log.debug("Ошибка при считывании из файла");
            bot.sendText(chatId, giveFileError());

            return currencyList;
        } catch (IncorrectCurrencyException e) {
            log.debug("Введенная валюта отсутствует в списке");
            bot.sendText(chatId, giveCurrencyError(Currency.values()));
            return currencyList;
        }
        log.info("Для валюты:{}, данные получены из файла", currency);
        return currencyList;

    }

    /**
     * Класс startPredict производит выбор функции для выполнения расчета предсказания курса валюты по выбранной глубине предсказания
     *
     * @param inputPeriod - срок предсказания
     */
    public Integer selectPeriod(String inputPeriod, Long chatId) {
        Integer countDays = Integer.parseInt(ZERO);

        try {
            Period period = Period.valueOf(inputPeriod.toUpperCase());
            log.debug("Введенный период есть в списке");
            switch (period) {
                case WEEK -> countDays = COUNT_DAYS_IN_WEEK;
                case TOMORROW -> countDays = ONE_DAY;
                case MONTH -> countDays = COUNT_DAYS_IN_MONTH;
            }

        } catch (IllegalArgumentException e) {
            log.debug("Введенный период отсутствует в списке");
            bot.sendText(chatId, givePeriodError(Period.values()));
            return countDays;
        }
        log.info("Для периода {} получено {} дней", inputPeriod, countDays);
        return countDays;
    }


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
                    countDays = selectPeriod(inputParamPeriod, chatId);
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
                    predictResult = lastYearPredict.predict(item.getValue(), dateListMinusOneYear, formatter);
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
                    predictResult = lastYearPredict.predict(item.getValue(), dateListWithRandomYear, formatter);
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

    public String startOutputResult(List<PredictResult> predictResult, String paramOutput){

        String outputString = EMPTY_STRING;
        Output output = Output.valueOf(paramOutput.toUpperCase());
        log.debug("Начался выбор вида вывода результата");
        switch (output) {
            case GRAPH -> {
                try {
                    log.debug("Начался процесс формирования графика");
                    outputString = saveToFile(createGraph.createGraph(predictResult));
                } catch (IOException e) {
                    log.debug("Ошибка в работе программы");
                    throw new RuntimeException(e);
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

    /**
     * startProgram - получает данные из терминала и запускает расчет курса валюты
     */
    public String startProgram(String inputMessage, Long chatId) {
        String output = EMPTY_STRING;
        try {
            try {
                if (!checkCorrectInput(inputMessage)){
                    throw new IncorrectInputException(giveFormatError());
                }
                log.debug("Получено сообщение:{}",inputMessage);
                String paramOutput = DEFAULT_OUTPUT_PARAM;
                String[] inputMessageSeparated = inputMessage.split(Constants.SPACE);
                String firstWord = inputMessageSeparated[INDEX_FIRST_INPUT_WORD];
                if (!STANDARD_FIRST_WORD.equals(firstWord)) {
                    log.info("Первое слово не {},a {}", STANDARD_FIRST_WORD, firstWord);
                    throw new IncorrectInputException(giveFirstWordError());
                }
                String[] currencies = inputMessageSeparated[INDEX_SECOND_INPUT_WORD].split(COMMA);
                log.info("Получены следующие валюты {}",inputMessageSeparated[INDEX_SECOND_INPUT_WORD]);
                String inputPredictPeriod = inputMessageSeparated[INDEX_THIRD_INPUT_WORD].replace(DASH, EMPTY_STRING);
                String inputParamPeriod = inputMessageSeparated[INDEX_FORTH_INPUT_WORD];
                log.info("Выбран следующий период/дата предсказания: {}", inputParamPeriod);
                String predictAlgorithm = inputMessageSeparated[INDEX_FIFTH_INPUT_WORD];
                log.info("Выбран следующий алгоритм предсказания:{}", predictAlgorithm);
                if (inputMessageSeparated.length > INDEX_SIXTH_INPUT_WORD) {
                    paramOutput = inputMessageSeparated[INDEX_SEVENTH_INPUT_WORD];
                }
                log.info("Выбран следующий вариант вывода курса валют:{}",paramOutput);
                Map<String, List<CourseTable>> currencyTables = new HashMap<>();
                log.debug("Началось формирование словаря с валютами и курсам");
                for (String s : currencies) {
                    currencyTables.put(s, selectCurrency(s, chatId));
                }
                log.debug("Сформирован словарь с валютами и курсам валют. Его размер: {}", currencyTables.size());
                if (currencyTables.isEmpty()) {
                    log.debug("Словарь оказался пустой");
                    bot.sendText(chatId, giveDataError());
                }
                log.debug("Начался процесс предсказания курса валют");
                List<PredictResult> predictResult = startPredict(currencyTables, inputPredictPeriod, inputParamPeriod, predictAlgorithm, chatId);
                if (predictResult.isEmpty()) {
                    log.debug("Курса валют не получилось предсказать");
                    bot.sendText(chatId, giveDataError());
                }
                log.debug("Процесс предсказания курса валют закончился");
                log.debug("Начался процесс подготовки результата для вывода");
                output = startOutputResult(predictResult, paramOutput);
                log.debug("Процесс подготовки результата для вывода закончился");
            } catch (IncorrectInputException ex) {
                log.debug("Ошибка при формировании данных");
                bot.sendText(chatId, giveFormatError());

            }
        } catch (RuntimeException ex) {
            log.debug("Ошибка в работе программы");
            bot.sendText(chatId, "Ошибка в работе программы");
            ex.printStackTrace();
        }
        return output;
    }
}
