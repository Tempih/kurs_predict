package ru.liga.curspredict.utils;

import ru.liga.curspredict.structure.CursTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final String DELIMITER = ";";//todo сделай для этих констант отдельный класс чтобы было меньше дублирования
    private static final String SPACE = " ";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String EMPTY = "";
    /**
     * Класс getDataFromFile производит извлечение данных курса валюты из csv файла
     *
     * @param filePath - путь до файла с данными
     */
    public List<CursTable> getDataFromFile(String filePath) {
            List<CursTable> currencyList = new ArrayList<>();
            int nominal;
            String cdx, date;
            BigDecimal curs;
            String[] lineList;

            InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

            try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);//todo возможен NullPointerException
                 BufferedReader reader = new BufferedReader(streamReader))
            {
                  reader.readLine();

                for (String line; (line = reader.readLine()) != null;) {
                    lineList = line.split(DELIMITER);
                    nominal = Integer.parseInt(lineList[0].replace(SPACE, EMPTY));//todo магическое число
                    date = lineList[1];//todo магическое число
                    curs = new BigDecimal(lineList[2].replace(COMMA, DOT));//todo магическое число
                    cdx = lineList[3];

                    currencyList.add(new CursTable(nominal, date, curs, cdx));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return currencyList;
    }
}
