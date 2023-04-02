package ru.liga.coursepredict.formatter;

import org.apache.commons.text.WordUtils;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.structure.PredictResult;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static ru.liga.coursepredict.constants.Constants.DASH;
import static ru.liga.coursepredict.constants.Constants.ZERO;

public class Formatter {
    private static final Locale RU = new Locale("ru");
    private static final Integer ONE_DAY = 1;
    private static final Integer ONE_MONTH = 1;
    private static final Integer ONE_YEAR = 1;
    private static final Integer DIVIDER_FOR_UNIX = 1000;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final Integer INTEGER_PART = 0;
    private static final Integer DECIMAL_PART = 1;
    private static final Integer START_OF_DECIMAL_PART = 0;
    private static final Integer END_OF_DECIMAL_PART = 2;
    private static final String REGEX_EXPRESSION = "\\";
    private static final String EMPTY_STRING = "";
    private static final Integer LENGTH_AFTER_SPLIT_WITH_INTEGER_AND_DECIMAL_PART = 2;
    private static final Integer LENGTH_AFTER_SPLIT_WITHOUT_DECIMAL_PART = 1;
    private static final Integer LENGTH_AFTER_SPLIT_WITHOUT_INTEGER_AND_DECIMAL_PART = 0;
    private static final Integer START_INDEX_DAY = 0;
    private static final Integer START_INDEX_YEAR = 6;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();


    /**
     * addDayOfWeek производит получение дня недели из даты и возвращает [день недели дата](Вс 19.03.2023)
     *
     * @param date - дата
     */
    public String addDayOfWeek(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate currentDate = LocalDate.parse(date, formatter);
        DayOfWeek day = currentDate.getDayOfWeek();
        String upperDay = WordUtils.capitalize(day.getDisplayName(TextStyle.SHORT, RU));

        return upperDay.concat(Constants.SPACE).concat(date);
    }

    /**
     * roundCursWithSubsting производит обрезание размерности числа до 2 знаков
     *
     * @param curs - значение для округления
     */
    public String roundCursWithSubstring(BigDecimal curs) {
        String[] cursList = curs.toString().split(REGEX_EXPRESSION.concat(Constants.DOT));
        String integerPart = cursList[INTEGER_PART];
        String decimalPart = cursList[DECIMAL_PART].substring(START_OF_DECIMAL_PART, END_OF_DECIMAL_PART);
        return integerPart.concat(Constants.DOT).concat(decimalPart);
    }


    /**
     * checkLenghtOfCurs производит проверку длины дробной части значения и добавляет 0, если длина меньше 2
     *
     * @param curs - значение
     */
    public String checkLengthOfCurs(String curs) {
        String[] separationCurs = curs.split(Constants.COMMA);
        String decimals = EMPTY_STRING;
        if (separationCurs.length == LENGTH_AFTER_SPLIT_WITH_INTEGER_AND_DECIMAL_PART) {
            decimals = curs.split(Constants.COMMA)[DECIMAL_PART];
        }
        if (decimals.length() == LENGTH_AFTER_SPLIT_WITHOUT_DECIMAL_PART) {
            return curs.concat(ZERO);
        }
        if (decimals.length() == LENGTH_AFTER_SPLIT_WITHOUT_INTEGER_AND_DECIMAL_PART) {
            return curs.concat(Constants.COMMA).concat(ZERO).concat(ZERO);
        }
        return curs;
    }


    /**
     * convertDate подготавливает данные для вывода в формате [дата - занчение курса валют](Пт 24.03.2023 - 40,01)
     *
     * @param outputDate - дата
     * @param newCurs    - значение курса валют
     */
    public String convertDate(String outputDate, BigDecimal newCurs) {
        String date = addDayOfWeek(outputDate);
        String curs = checkLengthOfCurs(roundCursWithSubstring(newCurs).replace(Constants.DOT, Constants.COMMA));
        return date.concat(Constants.SPACE).concat(DASH).concat(Constants.SPACE).concat(curs);
    }

    /**
     * formatOutputDate создает даты для предсказанных курсов валют
     *
     * @param lastDate  - последняя дата расчета валюты в файле
     * @param countDate - кол-во днея для предсказания
     */
    public List<String> formatOutputDate(String lastDate, Integer countDate) {
        List<String> outputDates = new ArrayList<>();
        for (int i = 0; i < countDate; i++) {
            lastDate = LocalDate.parse(lastDate, FORMATTER).plusDays(ONE_DAY).format(FORMATTER);
            outputDates.add(lastDate);
        }
        return outputDates;
    }

    /**
     * startFormatResult - создает строку в формате [дата] - [курс валюты] и добавляент его в список для вывода
     *
     * @param predictResult  - кол-во днея для предсказания
     */
    public List<String> startFormatResult(PredictResult predictResult) {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < predictResult.getPredictedCurrency().size(); i++) {
            resultList.add(convertDate(predictResult.getDates().get(i), predictResult.getPredictedCurrency().get(i)));
        }
        return resultList;
    }

    public List<String> subYearFromDate(List<String> dateList) {
        return dateList.stream()
                .map(date -> LocalDate.parse(date, FORMATTER).minusYears(ONE_YEAR).format(FORMATTER))
                .collect(Collectors.toList());
    }
    public List<String> randomYearForDate(List<String> dateList, Integer minYear, Integer maxYear) {
        return dateList.stream()
                .map(date -> LocalDate.parse(date.substring(START_INDEX_DAY, START_INDEX_YEAR).concat(Integer.toString(randomYear(minYear, maxYear))), FORMATTER).format(FORMATTER))
                .collect(Collectors.toList());
    }
    public Integer randomYear(Integer minYear, Integer maxYear){
        return ThreadLocalRandom.current().nextInt(minYear, maxYear+ONE_YEAR);
    }
    public List<String> subDaysFromDate(List<String> dateList, Integer subDays) {
        return dateList.stream()
                .map(date -> LocalDate.parse(date, FORMATTER).minusDays(subDays).format(FORMATTER))
                .collect(Collectors.toList());
    }

    public BigDecimal convertDateToUnixTime(String date){
       return new BigDecimal(LocalDate.parse(date, FORMATTER).atStartOfDay(ZONE_ID).toEpochSecond()/DIVIDER_FOR_UNIX);
    }

    public BigDecimal convertDateToUnixTimeMinusMonth(String date){
        return new BigDecimal(LocalDate.parse(date, FORMATTER).minusMonths(ONE_MONTH).atStartOfDay(ZONE_ID).toEpochSecond()/DIVIDER_FOR_UNIX);
    }
}