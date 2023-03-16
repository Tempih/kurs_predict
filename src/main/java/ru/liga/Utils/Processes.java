package ru.liga.Utils;

import ru.liga.Structure.kurs_table.Kurs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Processes {

    public static String addDayOfWeek(String date_string) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = format.parse(date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        if (day_of_week == 1)
            return "Пн " + date_string;
        if (day_of_week == 2)
            return "Вт " + date_string;
        if (day_of_week == 3)
            return "Ср " + date_string;
        if (day_of_week == 4)
            return "Чт " + date_string;
        if (day_of_week == 5)
            return "Пт " + date_string;
        if (day_of_week == 6)
            return "Сб " + date_string;
        if (day_of_week == 7)
            return "Вс " + date_string;

        return date_string;
    }

    public static Double avgSumOfrray(ArrayList<Double> array){
        return array.stream().reduce(0.0, Double::sum)/array.size();
    }

    public static Double roundCurs(Double curs){
        Double scale = Math.pow(10, 2);

        return Math.round(curs * scale) / scale;
    }
    public static String checkLenghtOfCurs(String curs){
        String decimals = curs.split(",")[1];
        if (decimals.length() == 0)
            return curs + "00";
        if (decimals.length() == 1)
            return curs + "0";
        return curs;
    }


    public static ArrayList<Double> lastSevenCurs(ArrayList<Kurs> currency_table) {
        ArrayList<Double> last_seven_curs = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            last_seven_curs.add(currency_table.get(i).getCurs());
        }
        return last_seven_curs;
    }

    public static String convertDate(String date_for_output, Double new_kurs){
        return Processes.addDayOfWeek(date_for_output) + " - " + checkLenghtOfCurs(roundCurs(new_kurs).toString().replace(".", ","));
    }
    public static void predictForWeek(ArrayList<Kurs> currency_table){
        ArrayList<Double> last_seven_predict = lastSevenCurs(currency_table);
        String date_for_output = currency_table.get(0).getData();

        for (int i = 0; i < 7; i++) {
            Double new_kurs = Processes.avgSumOfrray(last_seven_predict);
            last_seven_predict.add(0, new_kurs);
            last_seven_predict.remove(last_seven_predict.size() - 1);
            date_for_output = Integer.toString(Integer.parseInt(date_for_output.substring(0,2)) + 1) + date_for_output.substring(2,10);
            workWithTerminal.textOutput(convertDate(date_for_output, new_kurs));
        }
    }

    public static void predictForTomorrow(ArrayList<Kurs> currency_table) {
        String date_for_output = Integer.toString(Integer.parseInt(currency_table.get(0).getData().substring(0,2)) + 1) + currency_table.get(0).getData().substring(2,10);
        ArrayList<Double> last_seven_curs = lastSevenCurs(currency_table);
        Double new_kurs = Processes.avgSumOfrray(last_seven_curs);
        workWithTerminal.textOutput(convertDate(date_for_output, new_kurs));
    }
}
