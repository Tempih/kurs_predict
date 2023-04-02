package ru.liga.coursepredict.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.liga.coursepredict.Intefaces.DateValidator;
import ru.liga.coursepredict.structure.Period;
import ru.liga.coursepredict.structure.PredictAlgorithms;
import ru.liga.coursepredict.system.StageControl;
import ru.liga.coursepredict.validation.DateValidatorUsingLocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.toIntExact;
import static ru.liga.coursepredict.constants.Constants.*;
import static ru.liga.coursepredict.telegram.Constans.*;

@Slf4j
public class ButtonHandler {
    private static String periodParam;
    public static String date;
    public static String alg;
    public static String output;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    DateValidator validator = new DateValidatorUsingLocalDate(dateFormatter);
    private static List<String> cur = new ArrayList<>();
    private static final Keyboards keyboards = new Keyboards();
    String text = EMPTY;
    InlineKeyboardMarkup kb;

    public EditMessageText editMessageText(Long id, String text, int msgId, InlineKeyboardMarkup kb) {
        log.debug("Начинаем формировать EditMessageText");
        EditMessageText message = new EditMessageText();
        message.setChatId(id);
        message.setMessageId(toIntExact(msgId));
        message.setText(text);
        message.setReplyMarkup(kb);
        log.debug("Закончили формировать EditMessageText");
        return message;
    }

    public EditMessageText startRate(Long id, int msgId) {
        log.debug("Начинаем предсказание");
        cur = new ArrayList<>();
        periodParam = EMPTY;
        date = EMPTY;
        alg = EMPTY;
        output = EMPTY;
        String text = REDICT_RU;
        return editMessageText(id, text, msgId, keyboards.initKbForStartPredict());
    }

    public EditMessageText buttonTapRate(Long id, int msgId) {
        cur = new ArrayList<>();
        periodParam = EMPTY;
        date = EMPTY;
        alg = EMPTY;
        output = EMPTY;
        String text = CURRENCY_ADD;
        log.debug("Все переменный сделаны пустыми");
        return editMessageText(id, text, msgId, keyboards.createNewKeyBoard(cur, ADD));
    }

    public EditMessageText buttonTapCurrency(Long id, String data, int msgId) {
        log.debug("Добавляем валюту, получено значение:".concat(data));
        kb = null;
        if (data.equals(CURRENCIES)) {
            log.debug("Значение ".concat(data).concat(" равно ").concat(CURRENCIES));
            if (periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            }
            if (!periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            }
            if (!date.isEmpty()) {
                if (periodParam.equals(DATE)) {
                    text = text.concat(DATE_OF_PREDICT).concat(date);
                }
                if (periodParam.equals(PERIOD)) {
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                }
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoard(cur, ADD);
            log.debug("Возвращаем ответ");
            return editMessageText(id, text, msgId, kb);
        }
        if (!(data.equals(START_PREDICT) || data.equals(DEL)) && data.substring(data.length() - LENGTH_WORD_TO_DEL_FROM_CURRENCY).toLowerCase().equals(ADD)) {
            if (data.length() > LENGTH_WORD_TO_DEL_FROM_CURRENCY) {
                cur.add(data.substring(INDEX_OF_START, data.length() - LENGTH_WORD_TO_DEL_FROM_CURRENCY));
                log.debug("Валюту ".concat(data).concat(" добавили"));
            }
            if (periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            }
            if (!periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            }
            kb = keyboards.createNewKeyBoard(cur, ADD);
        }
        if (data.equals(DEL)) {
            log.debug("Переходи в режим удаления валют");
            text = DEL_CURRENCY.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            kb = keyboards.createNewKeyBoard(cur, DEL);
        }

        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }

    public EditMessageText buttonTapCurrencyDelete(Long id, String data, int msgId) {
        kb = null;
        log.debug("Удаляем валюту, получено значение:".concat(data));
        if (!(data.equals(ADD))) {
            cur.remove(data.substring(INDEX_OF_START, data.length() - LENGTH_WORD_TO_DEL_FROM_CURRENCY));
            log.debug("Валюту ".concat(data).concat(" удалили"));
            if (periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            }
            if (!periodParam.isEmpty()) {
                text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            }
            if (!date.isEmpty()) {
                if (periodParam.equals(DATE)) {
                    text = text.concat(DATE_OF_PREDICT).concat(date);
                }
                if (periodParam.equals(PERIOD)) {
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                }
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            if (cur.isEmpty()) {
                kb = keyboards.createNewKeyBoard(cur, ADD);
            } else {
                kb = keyboards.createNewKeyBoard(cur, DEL);
            }

        }
        if (data.equals(ADD)) {
            log.debug("Переходи в режим добавления валют");
            text = CURRENCY_ADD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            kb = keyboards.createNewKeyBoard(cur, ADD);
        }
        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }

    public EditMessageText buttonTapDateOrPeriod(Long id, String data, int msgId) {
        log.debug("Выбираем период или дата, получено значение:".concat(data));
        kb = null;
        if (cur.isEmpty()) {
            log.debug("Ни одна из валют не была выбрана");
            text = SELECT_MIN_ONE_CURRENCY.concat(CURRENCY_ADD).concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase());
            kb = keyboards.createNewKeyBoard(cur, ADD);
            log.debug("Возвращаем ответ");
            return editMessageText(id, text, msgId, kb);
        }
        if (data.equals(PARAM_PERIOD)) {
            if (periodParam.isEmpty()) {
                text = SELECT_PERIOD_OR_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            }
            if (!periodParam.isEmpty()) {
                text = SELECT_PERIOD_OR_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            }
            if (!date.isEmpty()) {
                if (periodParam.equals(DATE)) {
                    text = text.concat(DATE_OF_PREDICT).concat(date);
                }
                if (periodParam.equals(PERIOD)) {
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                }
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardForPeriodParam(periodParam);
        }
        if (!(data.equals(PARAM_PERIOD))) {
            periodParam = data;
            log.debug("Получили режим периода ".concat(data));
            text = SELECT_PERIOD_OR_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            kb = keyboards.createNewKeyBoardForPeriodParam(periodParam);
        }
        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }


    public EditMessageText buttonTapPeriod(Long id, String data, int msgId) {
        kb = null;
        log.debug("Выбираем период, получено значение:".concat(data));
        if (data.equals(PERIOD)) {
            log.debug("Формируем ответ по периоду");
            periodParam = data;
            text = SELECT_PERIOD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            if (!date.isEmpty()) {
                if (periodParam.equals(DATE)) {
                    text = text.concat(DATE_OF_PREDICT).concat(date);
                }
                if (periodParam.equals(PERIOD)) {
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                }
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardPeriod();
        }
        if (!(data.equals(PERIOD))) {
            log.debug("Формируем ответ по дате");
            periodParam = data;
            text = SELECT_PERIOD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
            if (!date.isEmpty()) {
                if (periodParam.equals(DATE)) {
                    text = text.concat(DATE_OF_PREDICT).concat(date);
                }
                if (periodParam.equals(PERIOD)) {
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                }
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardPeriod();
        }
        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }


    public EditMessageText buttonTapDate(Long id, String data, int msgId) {
        kb = null;
        log.debug("Получаем дату, получено значение:".concat(data));
        if (data.equals(DATE)) {

            periodParam = data;
            if (date.length() != LENGTH_OF_DATE) {
                date = EMPTY;
            }
            text = DATE_INPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardDateInput();
            log.debug("Отправляем ответ с кнопками для ввода даты");

        }
        if (data.equals(DEL_INT)) {
            log.debug("Удаляем последнее полученное число");
            date = date.substring(INDEX_OF_START, date.length() - ONE);
            if (date.length() == LENGTH_OF_DAY + ONE || date.length() == LENGTH_OF_DAY_AND_MONTH) {
                date = date.substring(INDEX_OF_START, date.length() - ONE);
            }
            text = DATE_INPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardDateInput();
        }
        if (data.equals(DEL_ALL)) {
            log.debug("Удаляем все введенное число");
            date = EMPTY;
            text = DATE_INPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            if (!output.isEmpty()) {
                text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
            }
            kb = keyboards.createNewKeyBoardDateInput();
        }
        if (!(data.equals(DEL_INT) || data.equals(DATE) || data.equals(DEL_ALL))) {
            if (date.length() != LENGTH_OF_DATE) {
                if (date.length() == LENGTH_OF_DAY || date.length() == LENGTH_OF_DAY_AND_MONTH) {
                    date = date.concat(DOT);
                }
                date = date.concat(data);
                if (date.length() == LENGTH_OF_DAY || date.length() == LENGTH_OF_DAY_AND_MONTH) {
                    date = date.concat(DOT);
                }
                text = DATE_INPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                if (!alg.isEmpty()) {
                    text = text.concat(ALG_OF_PREDICT).concat(alg);
                }
                if (!output.isEmpty()) {
                    text = text.concat(TYPE_OF_OUTPUT_SELECTED).concat(output);
                }
            }
            if (date.length() == LENGTH_OF_DATE) {
                text = DATE_INPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                if (!alg.isEmpty()) {
                    text = text.concat(ALG_OF_PREDICT).concat(alg);
                }
                if (!output.isEmpty()) {
                    text = text.concat(OUTPUT_TYPE).concat(output);
                }
            }
            log.debug("Добавили число к дате:".concat(data));
            kb = keyboards.createNewKeyBoardDateInput();
        }
        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }

    public EditMessageText buttonTapAlg(Long id, String data, int msgId) {
        log.debug("Получаем алгоритм расчета, получено значение:".concat(data));
        if (date.isEmpty()) {
            log.debug("Дата не заполнена");
            if (periodParam.equals(DATE)) {
                log.debug("Возвращаем кнопки для ввода даты");
                text = EMPTY_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
                kb = keyboards.createNewKeyBoardDateInput();
                return editMessageText(id, text, msgId, kb);
            }
            if (periodParam.equals(PERIOD)) {
                log.debug("Возвращаем кнопки для выбора периода");
                text = EMPTY_PERIOD.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
                kb = keyboards.createNewKeyBoardPeriod();
                if (Arrays.stream(Period.values()).map(Enum::toString).toList().toString().toLowerCase().contains(data.toLowerCase())) {
                    date = data;
                    text = TYPE_OF_ALG_PREDICT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
                    text = text.concat(PERIOD_OF_PREDICT).concat(date);
                    kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
                }
            }
            log.debug("Возвращаем ответ");
            return editMessageText(id, text, msgId, kb);
        }
        if (data.equals(ALG)) {
            log.debug("Значение ".concat(data).concat("не равно ").concat(ALG));
            if (periodParam.equals(DATE)) {
                log.debug("До этого была дата");
                if (!validator.isValid(date)) {
                    log.debug("Введена не правильная дата");
                    date = EMPTY;
                    text = ERROR_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                    if (!alg.isEmpty()) {
                        text = text.concat(ALG_OF_PREDICT).concat(alg);
                    }
                    if (!output.isEmpty()) {
                        text = text.concat(OUTPUT_TYPE).concat(output);
                    }
                    kb = keyboards.createNewKeyBoardDateInput();
                    log.debug("Возвращаем ответ");
                    return editMessageText(id, text, msgId, kb);
                }
                text = TYPE_OF_ALG_PREDICT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                if (!alg.isEmpty()) {
                    text = text.concat(ALG_OF_PREDICT).concat(alg);
                }
                if (!output.isEmpty()) {
                    text = text.concat(OUTPUT_TYPE).concat(output);
                }
                log.debug("Возвращаем кнопки для выбора алгоритма");
                kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
                log.debug("Возвращаем ответ");
                return editMessageText(id, text, msgId, kb);
            }
            if (periodParam.equals(PERIOD)) {
                log.debug("До этого была период");
                text = TYPE_OF_ALG_PREDICT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(PERIOD_OF_PREDICT).concat(date);
                if (!alg.isEmpty()) {
                    text = text.concat(ALG_OF_PREDICT).concat(alg);
                }
                if (!output.isEmpty()) {
                    text = text.concat(OUTPUT_TYPE).concat(output);
                }
                log.debug("Возвращаем кнопки для выбора алгоритма");
                kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
                log.debug("Возвращаем ответ");
                return editMessageText(id, text, msgId, kb);
            }
        }
        if (!data.equals(ALG)) {
            log.debug("Значение ".concat(data).concat(" равно ").concat(ALG));
            if (periodParam.equals(DATE)) {
                log.debug("До этого была дата");
                if (!validator.isValid(date)) {
                    log.debug("Введена не правильная дата");
                    date = EMPTY;
                    text = ERROR_DATE.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                    if (!output.isEmpty()) {
                        text = text.concat(OUTPUT_TYPE).concat(output);
                    }
                    kb = keyboards.createNewKeyBoardDateInput();
                    log.debug("Возвращаем ответ");
                    return editMessageText(id, text, msgId, kb);
                }
                text = TYPE_OF_ALG_PREDICT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(DATE_OF_PREDICT).concat(date);
                if (!output.isEmpty()) {
                    text = text.concat(OUTPUT_TYPE).concat(output);
                }
                kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
                log.debug("Возвращаем ответ");
                return editMessageText(id, text, msgId, kb);
            }
            if (periodParam.equals(PERIOD)) {
                log.debug("До этого была период");
                date = data;
                text = TYPE_OF_ALG_PREDICT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam).concat(PERIOD_OF_PREDICT).concat(date);
                if (!output.isEmpty()) {
                    text = text.concat(OUTPUT_TYPE).concat(output);
                }
                kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
                log.debug("Возвращаем ответ");
                return editMessageText(id, text, msgId, kb);
            }
        }

        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }

    public EditMessageText buttonTapOutput(Long id, String data, int msgId) {
        log.debug("Получаем метод вывода результата, получено значение:".concat(data));
        if (alg.isEmpty()) {
            log.debug("Алгоритм не выбран");
            text = ALG_ERROR.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
            if (Arrays.stream(PredictAlgorithms.values()).map(Enum::toString).toList().toString().toLowerCase().contains(data.toLowerCase())) {
                alg = data;
                log.debug("Алгоритм выбран");
                text = CURRENCY_SELECT.concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
                kb = keyboards.createNewKeyBoardOutput(output);
            }
            if (periodParam.equals(DATE)) {
                text = text.concat(DATE_OF_PREDICT).concat(date);
            }
            if (periodParam.equals(PERIOD)) {
                text = text.concat(PERIOD_OF_PREDICT).concat(date);
            }
            if (!alg.isEmpty()) {
                text = text.concat(ALG_OF_PREDICT).concat(alg);
            }
            log.debug("Возвращаем ответ");
            return editMessageText(id, text, msgId, kb);
        }
        if (data.equals(OUTPUT)) {
            log.debug("Значение ".concat(data).concat(" равно ").concat(OUTPUT));
            text = TYPE_OF_OUTPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            if (periodParam.equals(DATE)) {
                text = text.concat(DATE_OF_PREDICT).concat(date);
            }
            if (periodParam.equals(PERIOD)) {
                text = text.concat(PERIOD_OF_PREDICT).concat(date);
            }
            text = text.concat(ALG_OF_PREDICT).concat(alg);
            if (!output.isEmpty()) {
                text = text.concat(OUTPUT_TYPE).concat(output);
            }
            kb = keyboards.createNewKeyBoardAlg(date, periodParam, cur, alg);
            log.debug("Проверяем что можно рисовать график");
            if (cur.size() > ONE) {
                if (periodParam.equals(PERIOD)) {
                    if (date.equals(WEEK) || date.equals(MONTH)) {
                        log.debug("График рисовать можно");
                        kb = keyboards.createNewKeyBoardOutput(output);
                    }
                }
            }

        }
        if (Arrays.stream(PredictAlgorithms.values()).map(Enum::toString).toList().toString().toLowerCase().contains(data.toLowerCase())) {
            log.debug("Проверяем алгоритм");
            alg = data;
            text = TYPE_OF_OUTPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            if (periodParam.equals(DATE)) {
                text = text.concat(DATE_OF_PREDICT).concat(date);
            }
            if (periodParam.equals(PERIOD)) {
                text = text.concat(PERIOD_OF_PREDICT).concat(date);
            }
            text = text.concat(ALG_OF_PREDICT).concat(alg);
            if (!output.isEmpty()) {
                text = text.concat(OUTPUT_TYPE).concat(output);
            }
            kb = keyboards.createNewKeyBoardOutput(output);
            log.debug("Возвращаем ответ");
            return editMessageText(id, text, msgId, kb);
        }
        if (!(data.equals(OUTPUT))) {
            System.out.println(23);
            log.debug("Значение ".concat(data).concat(" не равно ").concat(OUTPUT));
            output = data;
            text = TYPE_OF_OUTPUT.concat(CURRENCY_SELECT).concat(cur.toString().toUpperCase()).concat(TYPE_OF_PERIOD_PREDICT).concat(periodParam);
            if (periodParam.equals(DATE)) {
                text = text.concat(DATE_OF_PREDICT).concat(date);
            }
            if (periodParam.equals(PERIOD)) {
                text = text.concat(PERIOD_OF_PREDICT).concat(date);
            }
            text = text.concat(ALG_OF_PREDICT).concat(alg);
            text = text.concat(OUTPUT_TYPE).concat(output);

            kb = keyboards.createNewKeyBoardOutput(output);
        }
        log.debug("Возвращаем ответ");
        return editMessageText(id, text, msgId, kb);
    }

    public String buttonTapResult(Long id) {
        log.debug("Начинаем формировать запрос");
        String input = RATE.concat(SPACE);
        for (String s : cur) {
            input = input.concat(s).concat(COMMA);
        }
        input = input.substring(INDEX_OF_START, input.length() - ONE);
        input = input.concat(SPACE).concat(DASH).concat(periodParam).concat(SPACE).concat(date).concat(SPACE).concat(DASH).concat(ALG).concat(SPACE).concat(alg);
        if (!output.isEmpty()) {
            input = input.concat(SPACE).concat(DASH).concat(OUTPUT).concat(SPACE).concat(output);
        }
        log.debug("Запрос сформирован:".concat(input));

        StageControl stageControl = new StageControl();
        log.debug("Начинаем расчет");
        String result = stageControl.startProgram(input, id);
        log.debug("Расчет окончен");
        return result;
    }
}
