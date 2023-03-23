package ru.liga.curspredict.exceptions;

public class Excepion {// todo этот класс-обертка лишний + ошибка в названии

    public static class IncorrectInput extends Exception {// todo не очень. Вынеси все эти исключения в отдельные файлы
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
