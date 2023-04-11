package ru.liga.coursepredict.controller;

import java.util.Scanner;

import static ru.liga.coursepredict.outputcreater.InfoOutput.giveInputInfo;

public class TerminalController {

    /**
     * Класс textOutput производит вывод сообщения в консоль
     *
     * @param message - сообщение для вывода в консоль
     */
    public void textOutput(String message) {
        System.out.println(message);
    }

    /**
     * Класс textInput производит считывание сообщения из консоли
     */
    public String textInput() {
        Scanner in = new Scanner(System.in);
        textOutput(giveInputInfo());
        return in.nextLine();
    }
}

