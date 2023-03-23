package ru.liga.curspredict.system;

import ru.liga.curspredict.exceptions.Excepion;
import ru.liga.curspredict.structure.CursTable;
import ru.liga.curspredict.structure.Enums.Currency;
import ru.liga.curspredict.structure.Enums.Period;
import ru.liga.curspredict.utils.Formatter;
import ru.liga.curspredict.utils.Parser;
import ru.liga.curspredict.utils.WorkWithTerminal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.liga.curspredict.utils.InfoOutput.*;
import static ru.liga.curspredict.utils.Calculations.predict;
import static ru.liga.curspredict.utils.ResultOutput.giveResult;

public class StageControl {

    private static final String MESSAGE_DELIMITER = " ";
    private static final String STANDARD_FIRST_WORD = "rate";
    private static final Parser parser = new Parser();
    private static final Formatter formatter = new Formatter();


    /**
     * Класс selectCurrency производит выбор файла для загрузки данных по выбранной валюте
     *
     * @param currency - валюта
     */
    public List<CursTable> selectCurrency(String currency) {
        List<CursTable> currencyList = new ArrayList<>();

        try {
            Currency.lookup(currency.toUpperCase());//todo смысл вызова метода?
            Currency currencies = Currency.valueOf(currency.toUpperCase());

            switch (currencies) {
                case USD -> currencyList = parser.getDataFromFile("usd.csv");//todo вынеси в константу
                case EUR -> currencyList = parser.getDataFromFile("eur.csv");//todo вынеси в константу
                case TRY -> currencyList = parser.getDataFromFile("try.csv");//todo вынеси в константу
                default -> throw new Excepion.IncorrectCurrency();
            }
        } catch (RuntimeException e) {
            WorkWithTerminal.textOutput(giveFileError());
            return currencyList;
        } catch (Excepion.IncorrectCurrency e) {
            WorkWithTerminal.textOutput(giveCurrencyError(Currency.values()));
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
        int countDay;
        List<BigDecimal> predictResult;
        List<String> resultList = new ArrayList<>();

        List<CursTable> currencyTable = new ArrayList<>(selectCurrency(currency));

        if (currencyTable.size() == 0) {//todo isEmpty
            return false;
        }

        String lastDate = currencyTable.get(0).getDate();//todo магическое число

        try {
            Period period = Period.valueOf(inputPeriod.toUpperCase());
            switch (period) {
                case WEEK -> {
                    countDay = 7;//todo магическое число
                    predictResult = predict(currencyTable, countDay);
                    if (predictResult == null){//todo делай реформат кода
                        return false;
                    }
                    resultList = formatter.startFormatResult(predictResult, lastDate, countDay);
                }
                case TOMORROW -> {
                    countDay = 1;
                    predictResult = predict(currencyTable, countDay);
                    if (predictResult == null){
                        WorkWithTerminal.textOutput(giveZeroDivider());
                        return false;
                    }
                    resultList = formatter.startFormatResult(predictResult, lastDate, countDay);
                }
            }
        } catch (IllegalArgumentException e) {
            WorkWithTerminal.textOutput(givePeriodError(Period.values()));
            return false;
        }
        if (resultList.size() != 0) {//todo isEmpty
            giveResult(resultList);
        } else {
            return false;
        }
        return true;
    }

    /**
     * startProgram - получает данные из терминала и запускает расчет курса валюты
     */
    public void startProgram() {
        while (true) {
            try {// todo while включи в блок try
                String inputMessage = WorkWithTerminal.textInput();
                String[] inputMessageSeparated = inputMessage.split(MESSAGE_DELIMITER);
                if (inputMessageSeparated.length != 3) {//todo магическое число
                    throw new Excepion.IncorrectInput();
                }
                String firstWord = inputMessageSeparated[0];//todo магическое число
                if (!STANDARD_FIRST_WORD.equals(firstWord)) {
                    throw new Excepion.IncorrectInput(giveFirstWordError());
                }

                String currency = inputMessageSeparated[1];//todo магическое число
                String period = inputMessageSeparated[2];//todo магическое число


                Boolean exitStatus = startPredict(currency, period);
                if (exitStatus) {
                    break;
                }

            } catch (Excepion.IncorrectInput ex) {
                WorkWithTerminal.textOutput(giveFormatError(ex.getMessage()));
            }
        }


    }
}
