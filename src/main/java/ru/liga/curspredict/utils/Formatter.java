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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");//todo вынеси в константу
        LocalDate currentDate = LocalDate.parse(date, formatter);
        DayOfWeek day = currentDate.getDayOfWeek();
        String upperDay = WordUtils.capitalize(day.getDisplayName(TextStyle.SHORT, RU));

        return upperDay.concat(" ").concat(date);//todo вынеси в константу пробел
    }

    /**
     * roundCursWithSubsting производит обрезание размерности числа до 2 знаков
     *
     * @param curs - значение для округления
     */
    public String roundCursWithSubsting(BigDecimal curs) {//todo ошибка в названии метода
        String[] cursList = curs.toString().split("\\.");
        String integerPart = cursList[0];//todo магическое число
        String decimalPart = cursList[1].substring(0, 2);//todo магическое число
        return integerPart.concat(".").concat(decimalPart);//todo вынеси в константу точку
    }


    /**
     * checkLenghtOfCurs производит проверку длины дробной части значения и добавляет 0, если длина меньше 2
     *
     * @param curs - значение
     */
    public String checkLengthOfCurs(String curs) {
        String[] separationCurs = curs.split(COMMA);
        String decimals = "";
        if (separationCurs.length == 2) {//todo магическое число
            decimals = curs.split(COMMA)[1];//todo магическое число
        }
        if (decimals.length() == 1) {//todo магическое число
            return curs.concat(ZERO);
        }
        if (decimals.length() == 0) {//todo магическое число
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
        return date.concat(" - ").concat(curs);//todo вынеси в константу пробел
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
            mountYear = lastDate.substring(2, 10);//todo магическое число
            day = Integer.toString(Integer.parseInt(lastDate.substring(0, 2)) + 1);//todo магическое число
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
        List<String> resultList = new ArrayList();//todo добавь <> "new ArrayList<>();"
        for (int i = 0; i < countDay; i++) {
            resultList.add(convertDate(outputDates.get(i), newCurses.get(i)));
        }
        return resultList;
    }
}