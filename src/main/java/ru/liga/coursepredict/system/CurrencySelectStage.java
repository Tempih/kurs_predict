package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.enums.Currency;
import ru.liga.coursepredict.exceptions.IncorrectCurrencyException;
import ru.liga.coursepredict.model.CourseTable;
import ru.liga.coursepredict.parser.Parser;
import ru.liga.coursepredict.telegram.Sender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.liga.coursepredict.outputcreater.InfoOutput.giveCurrencyError;
import static ru.liga.coursepredict.outputcreater.InfoOutput.giveFileError;

@Slf4j

public class CurrencySelectStage {
    private static final Parser parser = new Parser();
    private static final Sender sender = new Sender();
    private static final String DIRECTORY = "course_data";
    private static final String USD_FILE = "usd.csv";
    private static final String LEV_FILE = "lev.csv";
    private static final String DRAM_FILE = "dram.csv";
    private static final String EUR_FILE = "eur.csv";
    private static final String TRY_FILE = "try.csv";

    public List<CourseTable> getCurrencyData(String currency, Long chatId) {
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
            sender.sendText(chatId, giveFileError());

            return currencyList;
        } catch (IncorrectCurrencyException e) {
            log.debug("Введенная валюта отсутствует в списке");
            sender.sendText(chatId, giveCurrencyError(Currency.values()));
            return currencyList;
        }
        log.info("Для валюты:{}, данные получены из файла", currency);
        return currencyList;

    }
}
