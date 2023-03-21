package ru.liga.Utils;

import ru.liga.Structure.kurs_table.Kurs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
public class FileProcces {
    /**
     *Класс getDataFromFile производит извлечение данных курса валюты из csv файла
     *@param path_to_file - путь до файла с данными
     */
    public ArrayList<Kurs> getDataFromFile(String path_to_file) {
        try {
            String DELIMITER = ";";

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream(path_to_file);
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);

            ArrayList<Kurs> currency_list =new ArrayList<>();

            reader.readLine();

            for (String line; (line = reader.readLine()) != null;) {
                currency_list.add(new Kurs(line.split(DELIMITER)));
            }
            reader.close();

            return currency_list;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
