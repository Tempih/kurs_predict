package ru.liga.curspredict.structure;

import ru.liga.curspredict.exceptions.Excepion;

public class Enums {// todo этот класс тут лишний, все вложенные классы вынеси в отдельный файл
    public static enum Period {//todo static не нужен
        WEEK,
        TOMORROW;//todo ";" не нужен
    }

    public static enum Currency {//todo static не нужен
        TRY,
        USD,
        EUR;

        public static Currency lookup(String id) throws Excepion.IncorrectCurrency {// todo не используется возвращаемое значение
            try {
                return Currency.valueOf(id);
            } catch (IllegalArgumentException e) {
                throw new Excepion.IncorrectCurrency();
            }
        }
    }
}

