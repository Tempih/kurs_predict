package ru.liga.System;

import ru.liga.Structure.kurs_table;
import ru.liga.Utils.FileProcces;

import java.util.ArrayList;

import static ru.liga.Utils.Processes.predictForTomorrow;
import static ru.liga.Utils.Processes.predictForWeek;

public class StageControl {
    public ArrayList<kurs_table.Kurs> selectCurrency(String currency, FileProcces fileProcces){
        ArrayList<kurs_table.Kurs> currency_table = null;
        if (currency.equals("USD")) {
            currency_table = fileProcces.getDataFromFile("src/main/resources/dollar.csv");
        }
        if (currency.equals("EUR")) {
            currency_table = fileProcces.getDataFromFile("src/main/resources/evro.csv");
        }
        if (currency.equals("TRY")) {
            currency_table = fileProcces.getDataFromFile("src/main/resources/turkish_lira.csv");
        }
        return currency_table;
    }

    public void startPredict(String deep_of_predict, ArrayList<kurs_table.Kurs> currency_table){
        if (deep_of_predict.equals("week")){
            predictForWeek(currency_table);
        }
        if (deep_of_predict.equals("tomorrow")){
            predictForTomorrow(currency_table);
        }
    }
}
