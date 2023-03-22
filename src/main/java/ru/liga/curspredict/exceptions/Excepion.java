package ru.liga.curspredict.exceptions;

public class Excepion {

    public static class IncorrectInput extends Exception {
        public IncorrectInput() {
            super();
        }
        public IncorrectInput(String message) {
            super(message);
        }
    }

    public static class IncorrectCurrency extends Exception {
        public IncorrectCurrency() {
            super();
        }
    }
}
