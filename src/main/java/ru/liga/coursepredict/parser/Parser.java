package ru.liga.coursepredict.parser;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.model.CourseTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.liga.coursepredict.constants.Constants.ZERO;

@Slf4j
public class Parser {
    private static final String DELIMITER = ";";
    private static final String EMPTY = "";
    public static final Integer ONE = 1;
    private static final Integer NOMINAL_INDEX = 0;
    private static final Integer DATE_INDEX = 1;
    private static final Integer CURS_INDEX = 2;
    private static final Integer CDX_INDEX = 3;
    private static final Integer YEAR_INDEX = 6;


    /**
     * Класс getDataFromFile производит извлечение данных курса валюты из csv файла
     *
     * @param filePath - путь до файла с данными
     */
    public List<CourseTable> getDataFromFile(String filePath) {
        List<CourseTable> currencyList = new ArrayList<>();
        int nominal;
        String cdx, date;
        BigDecimal curs;
        String[] lineList;
        log.debug("Начинаем парсить файл {}",filePath);
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

        try (InputStreamReader streamReader = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            log.debug("Читаем содержимое файла");
            reader.readLine();
            for (String line; (line = reader.readLine()) != null; ) {
                lineList = line.split(DELIMITER);
                nominal = Integer.parseInt(lineList[NOMINAL_INDEX].replace(Constants.SPACE, EMPTY));
                date = lineList[DATE_INDEX];
                curs = new BigDecimal(lineList[CURS_INDEX].replace(Constants.COMMA, Constants.DOT));
                cdx = lineList[CDX_INDEX];

                currencyList.add(new CourseTable(nominal, date, curs, cdx));
            }
            log.debug("Прочитали содержимое файла {}", filePath);
        } catch (IOException | NullPointerException e) {
            log.debug("Ошибка в читаемом файле {}", filePath);
            e.printStackTrace();
        }
        log.debug("Закончили парсить файл {}", filePath);
        return currencyList;
    }

    public Integer getMinYear(List<CourseTable> courseTable) {
        return Integer.parseInt(courseTable.get(courseTable.size() - ONE).getDate().substring(YEAR_INDEX));
    }

    public Integer getMaxYear(List<CourseTable> courseTable) {
        return Integer.parseInt(courseTable.get(Integer.parseInt(ZERO)).getDate().substring(YEAR_INDEX));
    }
}
