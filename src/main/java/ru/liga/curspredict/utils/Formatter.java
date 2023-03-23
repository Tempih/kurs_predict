package ru.liga.curspredict.utils;

import org.apache.commons.text.WordUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Formatter {
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String ZERO = "0";
    private static final Locale RU = new Locale("ru");

    /**
     * addDayOfWeek производит получение дня недели из даты и возвращает [день недели дата](Вс 19.03.2023)
     *
     * @param date - дата
     */
    public String addDayOfWeek(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDate = LocalDate.parse(date, formatter);
        DayOfWeek day = currentDate.getDayOfWeek();
        String upperDay = WordUtils.capitalize(day.getDisplayName(TextStyle.SHORT, RU));

        return upperDay.concat(" ").concat(date);
    }

    /**
     * roundCursWithSubsting производит обрезание размерности числа до 2 знаков
     *
     * @param curs - значение для округления
     */
    public String roundCursWithSubsting(BigDecimal curs) {
        String[] cursList = curs.toString().split("\\.");
        String integerPart = cursList[0];
        String decimalPart = cursList[1].substring(0, 2);
        return integerPart.concat(".").concat(decimalPart);
    }


    /**
     * checkLenghtOfCurs производит проверку длины дробной части значения и добавляет 0, если длина меньше 2
     *
     * @param curs - значение
     */
    public String checkLengthOfCurs(String curs) {
        String[] separationCurs = curs.split(COMMA);
        String decimals = "";
        if (separationCurs.length == 2) {
            decimals = curs.split(COMMA)[1];
        }
        if (decimals.length() == 1) {
            return curs.concat(ZERO);
        }
        if (decimals.length() == 0) {
            return curs.concat(COMMA).concat(ZERO).concat(ZERO);
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
        String curs = checkLengthOfCurs(roundCursWithSubsting(newCurs).replace(DOT, COMMA));
        return date.concat(" - ").concat(curs);
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
            mountYear = lastDate.substring(2, 10);
            day = Integer.toString(Integer.parseInt(lastDate.substring(0, 2)) + 1);
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
        List<String> resultList = new ArrayList();
        for (int i = 0; i < countDay; i++) {
            resultList.add(convertDate(outputDates.get(i), newCurses.get(i)));
        }
        return resultList;
    }
}