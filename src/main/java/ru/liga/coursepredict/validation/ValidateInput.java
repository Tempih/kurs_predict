package ru.liga.coursepredict.validation;

public class ValidateInput {
    public static boolean checkCorrectInput(String message){
        if (!message.contains("rate")){
            return false;
        }
        if(!(message.contains("-date") || message.contains("-period"))){
            return false;
        }
        if(!(message.contains("-alg"))){
            return false;
        }
        if(message.contains("graph") && (message.split(" ")[1].split(",").length < 2 || (message.contains("tomorrow")|| message.contains("-date")))){
            return false;
        }
        return true;
    }
}
