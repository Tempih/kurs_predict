package ru.liga.Utils;

import java.util.Scanner;

public class workWithTerminal {

    public static void textOutput(String message){
        System.out.println(message);
    }

    public static String textInput(){
        Scanner in = new Scanner(System.in);
        System.out.print("Input a what do your want to now: ");
        return in.nextLine();
    }
}

