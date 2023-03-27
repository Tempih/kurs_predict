package ru.liga.coursepredict.formatter;

import org.apache.commons.text.WordUtils;
import ru.liga.coursepredict.constants.Constants;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Formatter {
    private static final String ZERO = "0";
    private static final Locale RU = new Locale("ru");
    private static final Integer ONE_DAY = 1;
    private static final Integer DAY_START_INDEX = 0;
    private static final Integer MOUNT_AND_YEAR_START_INDEX = 2;
    private static final Integer MOUNT_AND_YEAR_END_INDEX = 10;
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
    private static final String DASH = "-";


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
        String mountYear, day;
        for (int i = 0; i < countDate; i++) {
            mountYear = lastDate.substring(MOUNT_AND_YEAR_START_INDEX, MOUNT_AND_YEAR_END_INDEX);
            day = Integer.toString(Integer.parseInt(lastDate.substring(DAY_START_INDEX, MOUNT_AND_YEAR_START_INDEX)) + ONE_DAY);
            lastDate = day.concat(mountYear);
            outputDates.add(lastDate);
        }
        return outputDates;
    }

    /**
     * startFormatResult - создает строку в формате [дата] - [курс валюты] и добавляент его в список для вывода
     *
     * @param newCurses - предсказанные курсы валюты
     * @param lastDate  - последняя дата расчета валюты в файле
     * @param countDay  - кол-во днея для предсказания
     */
    public List<String> startFormatResult(List<BigDecimal> newCurses, String lastDate, Integer countDay) {
        List<String> outputDates = formatOutputDate(lastDate, countDay);
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < countDay; i++) {
            resultList.add(convertDate(outputDates.get(i), newCurses.get(i)));
        }
        return resultList;
    }
}