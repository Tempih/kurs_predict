package ru.liga.coursepredict.enums;

import ru.liga.coursepredict.exceptions.IncorrectCurrencyException;

public enum Currency {
    TRY,
    USD,
    LEV,
    DRAM,
    EUR;

    public static Currency lookup(String id) throws IncorrectCurrencyException {
        try {
            return Currency.valueOf(id);
        } catch (IllegalArgumentException e) {
            throw new IncorrectCurrencyException();
        }
    }
}

