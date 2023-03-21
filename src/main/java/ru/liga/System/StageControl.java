package ru.liga.System;

import ru.liga.Structure.kurs_table;
import ru.liga.Utils.FileProcces;
import ru.liga.Utils.workWithTerminal;

import java.util.ArrayList;

import static ru.liga.System.Main.main;
import static ru.liga.Utils.Processes.predictForTomorrow;
import static ru.liga.Utils.Processes.predictForWeek;
public class StageControl {
    /**
     *Класс selectCurrency производит выбор файла для загрузки данных по выбранной валюте
     *@param currency - валюта
     *@param fileProcces - класс для загрузки данных из файла
     */
    public ArrayList<kurs_table.Kurs> selectCurrency(String currency, FileProcces fileProcces){
        try {
        switch (currency.toLowerCase()) {
            case "usd":
                return fileProcces.getDataFromFile("usd.csv");
            case "eur":
                return fileProcces.getDataFromFile("eur.csv");
            case "try":
                return fileProcces.getDataFromFile("try.csv");
            default: {
                workWithTerminal.textOutput("\nДанная валюта отсутсвует в базе!\n");
                workWithTerminal.textOutput("Доступные валюты:\nUSD\nEUR\nTRY\n");
                main(null);
                return null;
            }
        }
             } catch (RuntimeException e) {
                 workWithTerminal.textOutput("\nФайл с данными отстсвует или пустой!\n"+ e.getMessage());
                return null;
        }
    }

    /**
     *Класс startPredict производит выбор функции для выполнения расчета предсказания курса валюты по выбранной глубине предсказания
     *@param deep_of_predict - глубина предсказания
     *@param currency_table - данные курса валюты
     */
    public void startPredict(String deep_of_predict, ArrayList<kurs_table.Kurs> currency_table){
        try {
            switch (deep_of_predict.toLowerCase()) {
                case "week" -> predictForWeek(currency_table);
                case "tomorrow" -> predictForTomorrow(currency_table);
                default -> {
                    workWithTerminal.textOutput("\nВведен неправильный срок предсказания!\n");
                    workWithTerminal.textOutput("Достпные сроки для предсказания:\nweek - предсказание на неделю\ntomorrow - предсказание на завтра\n");
                    main(null);
                }
            }
        } catch (NullPointerException e){
            workWithTerminal.textOutput("\nОшибка в данных!");
        }
    }
}
