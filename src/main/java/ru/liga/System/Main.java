package ru.liga.System;


import ru.liga.Utils.FileProcces;
import ru.liga.Utils.workWithTerminal;

public class Main {
    public static void main(String[] args) {
        FileProcces fileProcces = new FileProcces();
        StageControl stageControl = new StageControl();

        try {
        String input_message = workWithTerminal.textInput();
        String[] input_message_list = input_message.split(" ");

        if (!"rate".equals(input_message_list[0])) {
            throw new ArrayIndexOutOfBoundsException("Первое слово не rate!");
        }

        stageControl.startPredict(input_message_list[2], stageControl.selectCurrency(input_message_list[1], fileProcces));
        System.exit(0);
        }
        catch (ArrayIndexOutOfBoundsException ex){
            workWithTerminal.textOutput("\nЗапрос введен некорректно! Формат запроса: rate {валюта} {глубина предсказания})\n");
            main(null);
        }
    }

    }


