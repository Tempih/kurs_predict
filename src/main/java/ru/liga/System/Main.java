package ru.liga.System;


import ru.liga.Utils.FileProcces;
import ru.liga.Utils.workWithTerminal;

public class Main {
    public static void main(String[] args) {
        FileProcces fileProcces = new FileProcces();
        StageControl stageControl = new StageControl();
        String[] input_message_list = workWithTerminal.textInput().split(" ");
        stageControl.startPredict(input_message_list[2], stageControl.selectCurrency(input_message_list[1], fileProcces));

    }

    }


