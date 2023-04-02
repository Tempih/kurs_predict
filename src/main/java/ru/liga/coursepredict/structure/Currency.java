package ru.liga.coursepredict.structure;

import ru.liga.coursepredict.exceptions.IncorrectCurrency;

public enum Currency {
    TRY,
    USD,
    LEV,
    DRAM,
    EUR;

    public static Currency lookup(String id) throws IncorrectCurrency {
        try {
            return Currency.valueOf(id);
        } catch (IllegalArgumentException e) {
            throw new IncorrectCurrency();
        }
    }
}

