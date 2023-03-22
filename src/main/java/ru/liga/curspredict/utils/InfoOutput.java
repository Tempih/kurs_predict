package ru.liga.curspredict.utils;

import ru.liga.curspredict.structure.Enums.Currency;
import ru.liga.curspredict.structure.Enums.Period;

public class InfoOutput {
    private static final String dataError = "Ошибка в данных!";
    private static final String currencyError = "Данная валюта отсутсвует в базе!";
    private static final String currencyInfo = "Доступные валюты:";
    private static final String fileError = "Файл с данными отстсвует или пустой!";
    private static final String lineBreaker = "\n";
    private static final String periodError = "Введен неправильный срок предсказания!";
    private static final String periodInfo = "Достпные сроки для предсказания:";
    private static final String firstWordError = "Первое слово не rate!";
    private static final String formatInputError = "Запрос введен некорректно! Формат запроса: rate {валюта} {глубина предсказания})";
    private static final String inputInfo = "Введите запрос для предсказания: ";
    private static final String zeroDivider = "Произошло деление на 0";

    public static String giveDataError() {
        return lineBreaker.concat(dataError).concat(lineBreaker);
    }

    public static String giveFileError(String message) {
        return lineBreaker.concat(fileError).concat(lineBreaker).concat(message).concat(lineBreaker);
    }

    public static String giveCurrencyError(Currency[] currencyList) {
        StringBuilder errorText = new StringBuilder(lineBreaker.concat(currencyError).concat(lineBreaker).concat(currencyInfo).concat(lineBreaker));
        for (Currency currency : currencyList) {
            errorText.append(currency.toString().toUpperCase().concat(lineBreaker));
        }
        return errorText.toString();
    }

    public static String givePeriodError(Period[] periodList) {
        StringBuilder errorText = new StringBuilder(lineBreaker.concat(periodError).concat(lineBreaker).concat(periodInfo).concat(lineBreaker));
        for (Period period : periodList) {
            errorText.append(period.toString().toLowerCase().concat(lineBreaker));
        }
        return errorText.toString();
    }

    public static String giveFirstWordError() {
        return firstWordError.concat(lineBreaker);
    }

    public static String giveFormatError(String message) {
        if (message==null){
            return lineBreaker.concat(formatInputError).concat(lineBreaker);
        }
        return lineBreaker.concat(message).concat(lineBreaker).concat(formatInputError).concat(lineBreaker);
    }

    public static String giveInputInfo() {
        return inputInfo;
    }

    public static String giveZeroDivider(){
        return lineBreaker.concat(zeroDivider).concat(lineBreaker);
    }
}
