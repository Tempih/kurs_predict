package ru.liga.coursepredict.formatter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.exceptions.IncorrectDateFormatException;
import ru.liga.coursepredict.model.PredictResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static ru.liga.coursepredict.constants.Constants.*;

@Slf4j
public class Formatter {
    private static final Integer ONE_DAY = 1;
    private static final Integer ONE_MONTH = 1;
    private static final Integer ONE_YEAR = 1;
    private static final Integer DIVIDER_FOR_UNIX = 1000;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final Integer DECIMAL_PART = 1;
    private static final String EMPTY_STRING = "";
    private static final Integer LENGTH_AFTER_SPLIT_WITH_INTEGER_AND_DECIMAL_PART = 2;
    private static final Integer LENGTH_AFTER_SPLIT_WITHOUT_DECIMAL_PART = 1;
    private static final Integer LENGTH_AFTER_SPLIT_WITHOUT_INTEGER_AND_DECIMAL_PART = 0;
    private static final Integer START_INDEX_DAY = 0;
    private static final Integer START_INDEX_YEAR = 6;
    private static final String DATE_FORMAT_WITH_DAY = "EE dd.MM.yyyy";
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();


    /**
     * addDayOfWeek производит получение дня недели из даты и возвращает [день недели дата](Вс 19.03.2023)
     *
     * @param date - дата
     * @return дата в формате [день недели дата](Вс 19.03.2023)
     */
    public String addDayOfWeek(String date) throws IncorrectDateFormatException {
        Date currentDate;
        try {
            currentDate = new SimpleDateFormat(DATE_FORMAT).parse(date);
        } catch (ParseException e) {
            throw new IncorrectDateFormatException();
        }
        return WordUtils.capitalize(new SimpleDateFormat(DATE_FORMAT_WITH_DAY, Locale.getDefault()).format(currentDate));
    }

    /**
     * addCursDecimal производит проверку длины дробной части значения и добавляет 0, если длина меньше 2
     *
     * @param curs - курс валюты
     * @return curs - курс валюты с 2 знаками после запятой
     */
    public String addCursDecimal(String curs) {
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
     * convertDate подготавливает данные для вывода в формате [дата - значение курса валют](Пт 24.03.2023 - 40,01)
     *
     * @param outputDate - дата
     * @param newCurs    - значение курса валют
     * @return строка в формате [дата - значение курса валют](Пт 24.03.2023 - 40,01)
     */
    public String convertDate(String outputDate, BigDecimal newCurs) throws IncorrectDateFormatException {
        String date = addDayOfWeek(outputDate);
        String curs = newCurs.setScale(2, RoundingMode.DOWN).toString().replace(".", ",");
        return date.concat(Constants.SPACE).concat(DASH).concat(Constants.SPACE).concat(curs);
    }

    /**
     * formatOutputDate создает даты для предсказанных курсов валют
     *
     * @param lastDate  - последняя дата расчета валюты в файле
     * @param countDate - кол-во дней для предсказания
     * @return outputDates - список дат для предсказания
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
     * @param predictResult - кол-во днея для предсказания
     * @return resultList - список строк в формате [дата] - [курс валюты]
     */
    public List<String> startFormatResult(PredictResult predictResult) {
        List<String> resultList = new ArrayList<>();
        log.debug("Начинаем формировать выходной результат");
        try {
            for (int i = 0; i < predictResult.getPredictedCurrency().size(); i++) {
                resultList.add(convertDate(predictResult.getDates().get(i), predictResult.getPredictedCurrency().get(i)));
            }
        } catch (IncorrectDateFormatException e) {
            log.debug("Ошибка в формате даты");
        }
        log.debug("Закончили формировать выходной результат");
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

    public Integer randomYear(Integer minYear, Integer maxYear) {
        return ThreadLocalRandom.current().nextInt(minYear, maxYear + ONE_YEAR);
    }

    public BigDecimal convertDateToUnixTime(String date) {
        return new BigDecimal(LocalDate.parse(date, FORMATTER).atStartOfDay(ZONE_ID).toEpochSecond() / DIVIDER_FOR_UNIX);
    }

    public BigDecimal convertDateToUnixTimeMinusMonth(String date) {
        return new BigDecimal(LocalDate.parse(date, FORMATTER).minusMonths(ONE_MONTH).atStartOfDay(ZONE_ID).toEpochSecond() / DIVIDER_FOR_UNIX);
    }
}