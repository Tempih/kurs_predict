package ru.liga.Utils;

import ru.liga.Structure.kurs_table.Kurs;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Processes {

    /**
     *Класс addDayOfWeek производит получение дня недели из даты и возвращает [день недели дата](Вс 19.03.2023)
     * @param date_string - дата
     */
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
        return switch (day_of_week) {
            case 1 -> "Вс " + date_string;
            case 2 -> "Пн " + date_string;
            case 3 -> "Вт " + date_string;
            case 4 -> "Ср " + date_string;
            case 5 -> "Чт " + date_string;
            case 6 -> "Пт " + date_string;
            case 7 -> "Сб " + date_string;
            default -> date_string;
        };
    }

    /**
     *Класс avgSumOfrray производит расчет среднего арефметического для ArrayList
     * @param array - дата
     */
    public static Double avgSumOfrray(ArrayList<Double> array){
        return array.stream().reduce(0.0, Double::sum)/array.size();
    }

    /**
     *Класс roundCurs производит округление значения в ближайшую сторону
     * @param curs - значение для округления
     */
    public static String roundCurs(Double curs){
        Double scale = Math.pow(10, 2);
        Double roundScale = (Math.round(curs * scale) / scale);
        return roundScale.toString();
    }

    /**
     *Класс roundCurs производит обрезание размерности числа до 2 знаков
     * @param curs - значение для округления
     */
    public static String roundCursWithSubsting(Double curs){
        String[] curs_list = curs.toString().split("\\.");
        return curs_list[0] + "." + curs_list[1].substring(0, 2);
    }

    /**
     *Класс checkLenghtOfCurs производит проверку длины дробной части значения и добавляет 0, если длина меньше 2
     * @param curs - значение
     */
    public static String checkLenghtOfCurs(String curs){
        String[] curs_list = curs.split(",");
        if (curs_list.length == 2) {
            String decimals = curs.split(",")[1];
            if (decimals.length() == 1)
                return curs + "0";
        } else{
            return curs + ",00";
        }
        return curs;
    }

    /**
     *Класс lastSevenCurs производит получение последних 7 расчетов курса валют
     * @param currency_table - ArrayList значений
     */
    public static ArrayList<Double> lastSevenCurs(ArrayList<Kurs> currency_table) {
        ArrayList<Double> last_seven_curs = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            last_seven_curs.add(currency_table.get(i).getCurs()/currency_table.get(i).getNominal());
        }
        return last_seven_curs;
    }

    /**
     *Класс convertDate подготавливает данные для вывода в формате [дата - занчение курса валют](Пт 24.03.2023 - 40,01)
     * @param date_for_output - дата
     * @param new_kurs - значение курса валбт
     */
    public static String convertDate(String date_for_output, Double new_kurs){
        DecimalFormat df = new DecimalFormat("#.##");
        return Processes.addDayOfWeek(date_for_output) + " - " + checkLenghtOfCurs(roundCursWithSubsting(new_kurs).replace(".", ",")); //Можно вместо функции roundCursWithSubsting использовать roundCurs, от этого результат поменятеся. Я думаю правильнее использовать roundCursWithSubsting
    }

    /**`
     *Класс convertDate производит расчет курса валют на неделю
     * @param currency_table - ArrayList значений курса валют
     */
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

    /**
     *Класс convertDate производит расчет курса валют на завтра
     * @param currency_table - ArrayList значений курса валют
     */
    public static void predictForTomorrow(ArrayList<Kurs> currency_table) {
        String date_for_output = Integer.toString(Integer.parseInt(currency_table.get(0).getData().substring(0,2)) + 1) + currency_table.get(0).getData().substring(2,10);
        ArrayList<Double> last_seven_curs = lastSevenCurs(currency_table);
        Double new_kurs = Processes.avgSumOfrray(last_seven_curs);
        workWithTerminal.textOutput(convertDate(date_for_output, new_kurs) + ";");
    }
}
