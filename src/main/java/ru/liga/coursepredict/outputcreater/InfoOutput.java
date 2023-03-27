package ru.liga.coursepredict.outputcreater;

import ru.liga.coursepredict.structure.Currency;
import ru.liga.coursepredict.structure.Period;

public class InfoOutput {
    private static final String DATA_ERROR = "Ошибка в данных!";
    private static final String CURRENCY_ERROR = "Данная валюта отсутсвует в базе!";
    private static final String CURRENCY_INFO = "Доступные валюты:";
    private static final String FILE_ERROR = "Файл с данными отстсвует или пустой!";
    private static final String LINE_BREAKER = "\n";
    private static final String PERIOD_ERROR = "Введен неправильный срок предсказания!";
    private static final String PERIOD_INFO = "Достпные сроки для предсказания:";
    private static final String FIRST_WORD_ERROR = "Первое слово не rate!";
    private static final String FORMAT_INPUT_ERROR = "Запрос введен некорректно! Формат запроса: rate {валюта} {глубина предсказания})";
    private static final String INPUT_INFO = "Введите запрос для предсказания: ";
    private static final String ZERO_DIVIDER = "Произошло деление на 0";

    public static String giveDataError() {
        return LINE_BREAKER.concat(DATA_ERROR).concat(LINE_BREAKER);
    }

    public static String giveFileError() {
        return LINE_BREAKER.concat(FILE_ERROR).concat(LINE_BREAKER);
    }

    public static String giveCurrencyError(Currency[] currencyList) {
        StringBuilder errorText = new StringBuilder(LINE_BREAKER.concat(CURRENCY_ERROR).concat(LINE_BREAKER).concat(CURRENCY_INFO).concat(LINE_BREAKER));
        for (Currency currency : currencyList) {
            errorText.append(currency.toString().toUpperCase().concat(LINE_BREAKER));
        }
        return errorText.toString();
    }

    public static String givePeriodError(Period[] periodList) {
        StringBuilder errorText = new StringBuilder(LINE_BREAKER.concat(PERIOD_ERROR).concat(LINE_BREAKER).concat(PERIOD_INFO).concat(LINE_BREAKER));
        for (Period period : periodList) {
            errorText.append(period.toString().toLowerCase().concat(LINE_BREAKER));
        }
        return errorText.toString();
    }

    public static String giveFirstWordError() {
        return FIRST_WORD_ERROR.concat(LINE_BREAKER);
    }

    public static String giveFormatError() {
        return LINE_BREAKER.concat(FORMAT_INPUT_ERROR).concat(LINE_BREAKER);
    }

    public static String giveInputInfo() {
        return INPUT_INFO;
    }

    public static String giveZeroDivider() {
        return LINE_BREAKER.concat(ZERO_DIVIDER).concat(LINE_BREAKER);
    }
}
