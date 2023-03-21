package ru.liga.Utils;

import java.util.Scanner;

public class workWithTerminal {

    /**
     *Класс textOutput производит вывод сообщения в консоль
     *@param message - сообщение для вывода в консоль
     */
    public static void textOutput(String message){
        System.out.println(message);
    }

    /**
     *Класс textInput производит считывание сообщения из консоли
     */
    public static String textInput(){
        Scanner in = new Scanner(System.in);
        System.out.print("Введите запрос для предсказания: ");
        return in.nextLine();
    }
}

