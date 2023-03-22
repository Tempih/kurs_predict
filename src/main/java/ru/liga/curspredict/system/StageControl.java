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

    private final String messageDelimiter = " ";
    private final String standardFirstWord = "rate";
    private final Parser parser = new Parser();
    private final Formatter formatter = new Formatter();


    /**
     * Класс selectCurrency производит выбор файла для загрузки данных по выбранной валюте
     *
     * @param currency - валюта
     */
    public List<CursTable> selectCurrency(String currency) {
        List<CursTable> currencyList = new ArrayList<>();

        try {
            Currency.lookup(currency.toUpperCase());
            Currency currencies = Currency.valueOf(currency.toUpperCase());

            switch (currencies) {
                case USD -> currencyList = parser.getDataFromFile("usd.csv");
                case EUR -> currencyList = parser.getDataFromFile("eur.csv");
                case TRY -> currencyList = parser.getDataFromFile("try.csv");
                default -> throw new Excepion.IncorrectCurrency();
            }
        } catch (RuntimeException e) {
            WorkWithTerminal.textOutput(giveFileError(e.getMessage()));
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

        if (currencyTable.size() == 0) {
            return false;
        }

        String lastDate = currencyTable.get(0).getDate();

        try {
            Period period = Period.valueOf(inputPeriod);
            switch (period) {
                case week -> {
                    countDay = 7;
                    predictResult = predict(currencyTable, countDay);
                    resultList = formatter.startFormatResult(predictResult, lastDate, countDay);
                }
                case tomorrow -> {
                    countDay = 1;
                    predictResult = predict(currencyTable, countDay);
                    resultList = formatter.startFormatResult(predictResult, lastDate, countDay);
                }
            }
        } catch (IllegalArgumentException e) {
            WorkWithTerminal.textOutput(givePeriodError(Period.values()));
            return false;
        }
        if (resultList.size() != 0) {
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
            try {
                String inputMessage = WorkWithTerminal.textInput();
                String[] inputMessageSeparated = inputMessage.split(messageDelimiter);
                if (inputMessageSeparated.length != 3) {
                    throw new Excepion.IncorrectInput();
                }
                String firstWord = inputMessageSeparated[0];
                if (!standardFirstWord.equals(firstWord)) {
                    throw new Excepion.IncorrectInput(giveFirstWordError());
                }

                String currency = inputMessageSeparated[1];
                String period = inputMessageSeparated[2];


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
