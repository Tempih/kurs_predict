package ru.liga.curspredict.utils;

import java.util.Scanner;

import static ru.liga.curspredict.utils.InfoOutput.giveInputInfo;

public class WorkWithTerminal {

    /**
     * Класс textOutput производит вывод сообщения в консоль
     *
     * @param message - сообщение для вывода в консоль
     */
    public static void textOutput(String message) {// todo почему static?
        System.out.println(message);
    }

    /**
     * Класс textInput производит считывание сообщения из консоли
     */
    public static String textInput() {// todo почему static?
        Scanner in = new Scanner(System.in);
        textOutput(giveInputInfo());
        return in.nextLine();
    }
}

