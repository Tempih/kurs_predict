package ru.liga.curspredict.structure;

import ru.liga.curspredict.exceptions.Excepion;

public class Enums {
    public static enum Period {
        WEEK,
        TOMORROW;
    }

    public static enum Currency {
        TRY,
        USD,
        EUR;

        public static Currency lookup(String id) throws Excepion.IncorrectCurrency {
            try {
                return Currency.valueOf(id);
            } catch (IllegalArgumentException e) {
                throw new Excepion.IncorrectCurrency();
            }
        }
    }
}

