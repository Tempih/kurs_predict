package ru.liga.coursepredict.telegram;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.coursepredict.enums.*;

import java.util.ArrayList;
import java.util.List;

import static ru.liga.coursepredict.constants.TelegramConstants.*;

@Slf4j
public class Keyboards {
    public InlineKeyboardMarkup keyboardM1;


    public InlineKeyboardMarkup initKbForStartPredict() {
        log.debug("Начинаем формировать kb для predict");
        InlineKeyboardButton rate = new InlineKeyboardButton();
        rate.setText(PREDICT_COURSE);
        rate.setCallbackData(RATE);
        keyboardM1 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(rate);
        rowsInline.add(rowInline);
        keyboardM1.setKeyboard(rowsInline);
        log.debug("Закончили формировать kb для predict");
        return keyboardM1;
    }

    private boolean checkCurrencyInList(List<String> currencies, Currency currency) {
        return currencies.toString().toLowerCase().contains(currency.toString().toLowerCase());
    }

    public InlineKeyboardMarkup createNewKeyBoard(List<String> currencies, String mod) {
        log.debug("Начинаем формировать kb для валют");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            if (mod.equals(ADD)) {
                if (!checkCurrencyInList(currencies, currency)) {
                    currenciesList.add(InlineKeyboardButton.builder()
                            .text(currency.toString().toUpperCase()).callbackData(currency.toString().toLowerCase().concat(StringUtils.capitalize(mod)))
                            .build());
                }
            }
            if (mod.equals(DEL)) {
                if (checkCurrencyInList(currencies, currency)) {
                    currenciesList.add(InlineKeyboardButton.builder()
                            .text(currency.toString().toUpperCase()).callbackData(currency.toString().toLowerCase().concat(StringUtils.capitalize(mod)))
                            .build());
                }
            }
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        back.setCallbackData(START_PREDICT);
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(NEXT_RU);
        next.setCallbackData(PARAM_PERIOD);
        InlineKeyboardButton option = new InlineKeyboardButton();
        if (mod.equals(ADD)) {
            option.setText(DEL_RU);
            option.setCallbackData(DEL);
        }
        if (mod.equals(DEL)) {
            option.setText(ADD_RU);
            option.setCallbackData(ADD);
        }

        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();

        for (int i = 0; i < currenciesList.size(); i = i + 3) {
            if (i + 2 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
                rowInlineM2.add(currenciesList.get(i + 2));
            } else if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }
        rowInlineM2.add(back);
        rowInlineM2.add(option);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для валют");
        return keyboardM3;
    }

    public InlineKeyboardMarkup createNewKeyBoardForPeriodParam(String periodParam) {
        log.debug("Начинаем формировать kb для валют выбора срока предсказания");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (PredictPeriod period : PredictPeriod.values()) {
            currenciesList.add(InlineKeyboardButton.builder()
                    .text(period.toString()).callbackData(period.toString().toLowerCase())
                    .build());
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        back.setCallbackData(CURRENCIES);
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(NEXT_RU);
        if (periodParam.equals(DATE)) {
            next.setCallbackData(DATE);
        }
        if (periodParam.equals(PERIOD)) {
            next.setCallbackData(PERIOD);
        }
        if (periodParam.isEmpty()) {
            next.setCallbackData(PARAM_PERIOD);
        }
        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();
        for (int i = 0; i < currenciesList.size(); i = i + 3) {
            if (i + 2 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
                rowInlineM2.add(currenciesList.get(i + 2));
            } else if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }
        rowInlineM2.add(back);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для валют выбора срока предсказания");
        return keyboardM3;
    }

    public InlineKeyboardMarkup createNewKeyBoardDateInput() {
        log.debug("Начинаем формировать kb для ввода даты");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            currenciesList.add(InlineKeyboardButton.builder()
                    .text(Integer.toString(i)).callbackData(Integer.toString(i))
                    .build());
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        back.setCallbackData(PARAM_PERIOD);
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(NEXT_RU);
        next.setCallbackData(ALG);
        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();
        for (int i = 0; i < currenciesList.size(); i = i + 3) {
            if (i + 2 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
                rowInlineM2.add(currenciesList.get(i + 2));
            } else if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }

        rowInlineM2.add(InlineKeyboardButton.builder()
                .text(AGAIN_RU).callbackData(DEL_ALL)
                .build());
        rowInlineM2.add(InlineKeyboardButton.builder()
                .text(Integer.toString(0)).callbackData(Integer.toString(0))
                .build());
        rowInlineM2.add(InlineKeyboardButton.builder()
                .text(StringUtils.capitalize(DEL)).callbackData(DEL_INT)
                .build());
        rowsInlineM2.add(rowInlineM2);
        rowInlineM2 = new ArrayList<>();
        rowInlineM2.add(back);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для ввода даты");
        return keyboardM3;
    }

    public InlineKeyboardMarkup createNewKeyBoardPeriod() {
        log.debug("Начинаем формировать kb для выбора периода");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (Period period : Period.values()) {
            currenciesList.add(InlineKeyboardButton.builder()
                    .text(period.toString()).callbackData(period.toString().toLowerCase())
                    .build());
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        back.setCallbackData(PARAM_PERIOD);
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(NEXT_RU);
        next.setCallbackData(ALG);
        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();
        for (int i = 0; i < currenciesList.size(); i = i + 2) {
            if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }
        rowInlineM2.add(back);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для выбора периода");
        return keyboardM3;
    }

    public InlineKeyboardMarkup createNewKeyBoardAlg(String date, String periodParam, List<String> currencies, String alg) {
        log.debug("Начинаем формировать kb для выбора алгоритма");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (PredictAlgorithms algorithms : PredictAlgorithms.values()) {
            currenciesList.add(InlineKeyboardButton.builder()
                    .text(algorithms.toString()).callbackData(algorithms.toString().toLowerCase())
                    .build());
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        if (periodParam.equals(DATE)) {
            back.setCallbackData(DATE);
        }
        if (periodParam.equals(PERIOD)) {
            back.setCallbackData(PERIOD);
        }
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(CALCULATION_RU);
        next.setCallbackData(RESULT);
        if (alg.isEmpty()) {
            next.setCallbackData(OUTPUT);
        }
        if (currencies.size() > 1) {
            if (periodParam.equals(PERIOD)) {
                if (date.equals(WEEK) || date.equals(MONTH)) {
                    next.setText(NEXT_RU);
                    next.setCallbackData(OUTPUT);
                }
            }
        }
        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();
        for (int i = 0; i < currenciesList.size(); i = i + 2) {
            if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }
        rowInlineM2.add(back);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для выбора алгоритма");
        return keyboardM3;
    }

    public InlineKeyboardMarkup createNewKeyBoardOutput(String output) {
        log.debug("Начинаем формировать kb для выбора вида вывода");
        List<InlineKeyboardButton> currenciesList = new ArrayList<>();
        for (Output outputs : Output.values()) {
            currenciesList.add(InlineKeyboardButton.builder()
                    .text(outputs.toString()).callbackData(outputs.toString().toLowerCase())
                    .build());
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(BACK_RU);
        back.setCallbackData(ALG);
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(CALCULATION_RU);
        next.setCallbackData(RESULT);
        if (!output.isEmpty()) {
            next.setCallbackData(RESULT.concat(output));
        }

        InlineKeyboardMarkup keyboardM3 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineM2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineM2 = new ArrayList<>();
        for (int i = 0; i < currenciesList.size(); i = i + 2) {
            if (i + 1 < currenciesList.size()) {
                rowInlineM2.add(currenciesList.get(i));
                rowInlineM2.add(currenciesList.get(i + 1));
            } else {
                rowInlineM2.add(currenciesList.get(i));
            }
            rowsInlineM2.add(rowInlineM2);
            rowInlineM2 = new ArrayList<>();
        }
        rowInlineM2.add(back);
        rowInlineM2.add(next);
        rowsInlineM2.add(rowInlineM2);
        keyboardM3.setKeyboard(rowsInlineM2);
        log.debug("Закончили формировать kb для выбора вида вывода");
        return keyboardM3;
    }
}
