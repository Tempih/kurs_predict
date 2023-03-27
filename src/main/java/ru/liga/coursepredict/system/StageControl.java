package ru.liga.coursepredict.system;

import ru.liga.coursepredict.calculations.Calculations;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.exceptions.IncorrectCurrency;
import ru.liga.coursepredict.exceptions.IncorrectInput;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.parser.Parser;
import ru.liga.coursepredict.printer.WorkWithTerminal;
import ru.liga.coursepredict.structure.CourseTable;
import ru.liga.coursepredict.structure.Currency;
import ru.liga.coursepredict.structure.Period;
import ru.liga.coursepredict.outputcreater.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.liga.coursepredict.outputcreater.InfoOutput.*;

public class StageControl {
    private static final String STANDARD_FIRST_WORD = "rate";
    private static final Parser parser = new Parser();
    private static final Formatter formatter = new Formatter();
    private static final Calculations calculations = new Calculations();
    private static final WorkWithTerminal workWithTerminal = new WorkWithTerminal();
    private static final ResultOutput resultOutput = new ResultOutput();
    private static final String USD_FILE = "usd.csv";
    private static final String EUR_FILE = "eur.csv";
    private static final String TRY_FILE = "try.csv";
    private static final Integer LAST_DATE_INDEX = 0;
    private static final Integer COUNT_DAYS_IN_WEEK = 7;
    private static final Integer ONE_DAY = 1;
    private static final Integer LENGTH_INPUT_TEXT_WITH_THREE_WORD = 3;
    private static final Integer INDEX_FIRST_INPUT_WORD = 0;
    private static final Integer INDEX_SECOND_INPUT_WORD = 1;
    private static final Integer INDEX_THIRD_INPUT_WORD = 2;

    /**
     * Класс selectCurrency производит выбор файла для загрузки данных по выбранной валюте
     *
     * @param currency - валюта
     */
    public List<CourseTable> selectCurrency(String currency) {
        List<CourseTable> currencyList = new ArrayList<>();

        try {
            Currency currencies = Currency.lookup(currency.toUpperCase());
            switch (currencies) {
                case USD -> currencyList = parser.getDataFromFile(USD_FILE);
                case EUR -> currencyList = parser.getDataFromFile(EUR_FILE);
                case TRY -> currencyList = parser.getDataFromFile(TRY_FILE);
                default -> throw new IncorrectCurrency();
            }
        } catch (RuntimeException e) {
            workWithTerminal.textOutput(giveFileError());
            return currencyList;
        } catch (IncorrectCurrency e) {
            workWithTerminal.textOutput(giveCurrencyError(Currency.values()));
            return currencyList;
        }
        return currencyList;

    }

    /**
     * Класс startPredict производит выбор функции для выполнения расчета предсказания курса валюты по выбранной глубине предсказания
     *
     * @param currency    - валюта
     * @param inputPeriod - срок предсказания
     */
    public Boolean startPredict(String currency, String inputPeriod) {
        List<BigDecimal> predictResult;
        List<String> resultList = new ArrayList<>();

        List<CourseTable> currencyTable = new ArrayList<>(selectCurrency(currency));

        if (currencyTable.isEmpty()) {
            workWithTerminal.textOutput(giveDataError());
            return false;
        }

        String lastDate = currencyTable.get(LAST_DATE_INDEX).getDate();

        try {
            Period period = Period.valueOf(inputPeriod.toUpperCase());
            switch (period) {
                case WEEK -> {
                    predictResult = calculations.predict(currencyTable, COUNT_DAYS_IN_WEEK);
                    if (predictResult.size() != COUNT_DAYS_IN_WEEK) {
                        workWithTerminal.textOutput(giveZeroDivider());
                        return false;
                    }
                    resultList = formatter.startFormatResult(predictResult, lastDate, COUNT_DAYS_IN_WEEK);
                }
                case TOMORROW -> {
                    predictResult = calculations.predict(currencyTable, ONE_DAY);
                    if (predictResult.size() != ONE_DAY) {
                        workWithTerminal.textOutput(giveZeroDivider());
                        return false;
                    }
                    resultList = formatter.startFormatResult(predictResult, lastDate, ONE_DAY);
                }
            }
        } catch (IllegalArgumentException e) {
            workWithTerminal.textOutput(givePeriodError(Period.values()));
            return false;
        }
        if (!resultList.isEmpty()) {
            resultOutput.giveResult(resultList, workWithTerminal);
        } else {
            return false;
        }
        return true;
    }

    /**
     * startProgram - получает данные из терминала и запускает расчет курса валюты
     */
    public void startProgram() {
        try {
            while (true) {
                try {
                    String inputMessage = workWithTerminal.textInput();
                    String[] inputMessageSeparated = inputMessage.split(Constants.SPACE);
                    if (inputMessageSeparated.length != LENGTH_INPUT_TEXT_WITH_THREE_WORD) {
                        throw new IncorrectInput();
                    }
                    String firstWord = inputMessageSeparated[INDEX_FIRST_INPUT_WORD];
                    if (!STANDARD_FIRST_WORD.equals(firstWord)) {
                        throw new IncorrectInput(giveFirstWordError());
                    }

                    String currency = inputMessageSeparated[INDEX_SECOND_INPUT_WORD];
                    String period = inputMessageSeparated[INDEX_THIRD_INPUT_WORD];


                    Boolean exitStatus = startPredict(currency, period);
                    if (exitStatus) {
                        break;
                    }
                } catch (IncorrectInput ex) {
                    workWithTerminal.textOutput(giveFormatError());
                }
            }
        } catch(RuntimeException ex){
            ex.printStackTrace();
        }
    }
}
