package ru.liga.coursepredict.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.coursepredict.enums.*;
import ru.liga.coursepredict.exceptions.IncorrectDateFormatException;
import ru.liga.coursepredict.system.StageControl;

import java.util.Arrays;

import static ru.liga.coursepredict.constants.TelegramConstants.*;

@Slf4j
public class UpdateHandler {
    private static final Bot bot = new Bot();
    private static final Keyboards keyboards = new Keyboards();
    private static final ButtonHandler buttonHandler = new ButtonHandler();
    private static final StageControl stageControl = new StageControl();
    private static final Sender sender = new Sender();
    private static final String CURRENCY_ADD_ENUM_STRING = Arrays.stream(Currency.values()).map(currency -> currency.toString().concat(ADD)).toList().toString().toLowerCase();
    private static final String CURRENCY_DEL_ENUM_STRING = Arrays.stream(Currency.values()).map(currency -> currency.toString().concat(DEL)).toList().toString().toLowerCase();
    private static final String OUTPUT_ENUM_STRING = Arrays.stream(Output.values()).map(Enum::toString).toList().toString().toLowerCase();
    private static final String ALGORITHMS_ENUM_STRING = Arrays.stream(PredictAlgorithms.values()).map(Enum::toString).toList().toString().toLowerCase();
    private static final String PERIOD_ENUM_STRING = Arrays.stream(Period.values()).map(Enum::toString).toList().toString().toLowerCase();
    private static final String PERIOD_PREDICT_ENUM_STRING = Arrays.stream(PredictPeriod.values()).map(Enum::toString).toList().toString();

    public void handleMessage(Message message) throws IncorrectDateFormatException {
        String messageText = message.getText();
        long chatId = message.getChatId();
        log.debug("Получили сообщение от чата {}: {} ", chatId, messageText);
        try {
            switch (messageText) {
                case COMMAND_MENU, MENU ->
                        bot.execute(sender.sendTextWithKb(chatId, PREDICT_COURSE, keyboards.initKbForStartPredict()));
                case COMMAND_START -> bot.execute(sender.sendText(chatId, ANSWER));
                default -> handleRateMessage(chatId, messageText);
            }
        } catch (TelegramApiException e) {
            throw new IncorrectDateFormatException();
        }
    }

    private void handleRateMessage(long chatId, String messageText) {
        try {
            if (messageText.contains(RATE)) {
                if (messageText.contains(GRAPH)) {
                    bot.execute(sender.sendPhoto(Long.toString(chatId), stageControl.startProgram(messageText, chatId)));
                } else {
                    bot.execute(sender.sendText(chatId, stageControl.startProgram(messageText, chatId)));
                }
            } else {
                bot.execute(sender.sendText(chatId, ANSWER));
            }
        } catch (TelegramApiException e) {
            log.debug("Ошибка в отправке сообщения");
        }
    }


    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = callbackQuery.getMessage().getText();
        log.debug("Получили ответ от чата {}: {} ", chatId, messageText);
        try {
            if (callData.equals(START_PREDICT)) {
                log.debug("Ответ {} совпал с {}", callData, START_PREDICT);
                bot.execute(buttonHandler.startRate(chatId, messageId));
            }
            if (callData.equals(RATE)) {
                log.debug("Ответ {} совпал с {}", callData, RATE);
                bot.execute(buttonHandler.buttonTapRate(chatId, messageId));
            }
            if (callData.equals(PARAM_PERIOD) || PERIOD_PREDICT_ENUM_STRING.equalsIgnoreCase(callData)){
                log.debug("Ответ {} попал в формирование buttonTapDateOrPeriod", callData);
                bot.execute(buttonHandler.buttonTapDateOrPeriod(chatId, callData, messageId));
            }
            if (callData.equals(DATE) || NUMBERS.contains(callData) || callData.contains(DEL_INT) || callData.equals(DEL_ALL)) {
                log.debug("Ответ {} попал в формирование buttonTapDate", callData);
                bot.execute(buttonHandler.buttonTapDate(chatId, callData, messageId));
            }
            if (callData.equals(PERIOD)) {
                log.debug("Ответ {} попал в формирование buttonTapPeriod", callData);
                bot.execute(buttonHandler.buttonTapPeriod(chatId, callData, messageId));
            }
            if (callData.equals(ALG) || PERIOD_ENUM_STRING.contains(callData.toLowerCase())) {
                log.debug("Ответ {} попал в формирование buttonTapCurrencyDelete", callData);
                bot.execute(buttonHandler.buttonTapAlg(chatId, callData, messageId));
            }
            if (callData.equals(OUTPUT) || OUTPUT_ENUM_STRING.contains(callData.toLowerCase()) || ALGORITHMS_ENUM_STRING.contains(callData.toLowerCase())) {
                log.debug("Ответ {} попал в формирование buttonTapOutput", callData);
                bot.execute(buttonHandler.buttonTapOutput(chatId, callData, messageId));
            }
            if (callData.equals(RESULT.concat(GRAPH))) {
                log.debug("Ответ {} совпал с {}", callData, RESULT.concat(GRAPH));
                bot.execute(sender.editMessage(chatId, messageText, messageId));
                bot.execute(sender.sendPhoto(Long.toString(chatId), buttonHandler.buttonTapResult(chatId)));
            }
            if (callData.equals(RESULT) || callData.equals(RESULT.concat(LIST))) {
                log.debug("Ответ {} совпал с {} или с {}", callData, RESULT, RESULT.concat(LIST));
                bot.execute(sender.editMessage(chatId, messageText, messageId));
                bot.execute(sender.sendText(chatId, buttonHandler.buttonTapResult(chatId)));
            }
            if (callData.equals(CURRENCIES) || CURRENCY_ADD_ENUM_STRING.contains(callData.toLowerCase()) || callData.equalsIgnoreCase(ADD)) {
                log.debug("Ответ {} попал в формирование buttonTapCurrency", callData);
                bot.execute(buttonHandler.buttonTapCurrency(chatId, callData, messageId));
            }
            if (callData.equalsIgnoreCase(DEL) || CURRENCY_DEL_ENUM_STRING.contains(callData.toLowerCase())) {
                log.debug("Ответ {} попал в формирование buttonTapCurrencyDelete", callData);
                bot.execute(buttonHandler.buttonTapCurrencyDelete(chatId, callData, messageId));
            }
        } catch (TelegramApiException e) {
            log.debug("Ошибка в отправке сообщения");
        }
    }

}
