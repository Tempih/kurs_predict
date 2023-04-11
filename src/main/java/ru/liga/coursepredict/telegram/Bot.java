package ru.liga.coursepredict.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.coursepredict.enums.*;
import ru.liga.coursepredict.system.StageControl;

import java.io.File;
import java.util.Arrays;

import static java.lang.Math.toIntExact;
import static ru.liga.coursepredict.constants.TelegramConstants.*;

@Slf4j
public class Bot extends TelegramLongPollingBot {
    private static final Keyboards keyboards = new Keyboards();
    private static final ButtonHandler buttonHandler = new ButtonHandler();
    private static final StageControl stageControl = new StageControl();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.debug("Получили сообщение от чата {}: {} ", chatId, messageText);
            if (messageText.equals(COMMAND_MENU) || messageText.equalsIgnoreCase(MENU)) {
                sendTextWithKb(chatId, PREDICT_COURSE, keyboards.initKbForStartPredict());
            } else if (messageText.equals(COMMAND_START)) {
                sendText(chatId, ANSWER);
            } else if(messageText.contains(RATE)) {
                if(messageText.contains(GRAPH)) {
                    sendPhoto(Long.toString(chatId), stageControl.startProgram(messageText, chatId));
                }
                else {
                    sendText(chatId, stageControl.startProgram(messageText, chatId));
                }
            }else {
                sendText(chatId, ANSWER);
            }
        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String messageText = update.getCallbackQuery().getMessage().getText();
            log.debug("Получили ответ от чата {}: {} ", chatId, messageText);
            try {
                if (callData.equals(START_PREDICT)) {
                    log.debug("Ответ {} совпал с {}", callData, START_PREDICT);
                    execute(buttonHandler.startRate(chatId, messageId));
                }
                if (callData.equals(RATE)) {
                    log.debug("Ответ {} совпал с {}", callData, RATE);
                    execute(buttonHandler.buttonTapRate(chatId, messageId));
                }
                if (callData.equals(CURRENCIES) || Arrays.stream(Currency.values()).map(currency -> currency.toString().concat(ADD)).toList().toString().toLowerCase().contains(callData.toLowerCase()) || callData.equalsIgnoreCase(ADD)) {
                    log.debug("Ответ {} попал в формирование buttonTapCurrency", callData);
                    execute(buttonHandler.buttonTapCurrency(chatId, callData, messageId));
                }
                if (callData.equalsIgnoreCase(DEL) || Arrays.stream(Currency.values()).map(currency -> currency.toString().concat(DEL)).toList().toString().toLowerCase().contains(callData.toLowerCase())) {
                    log.debug("Ответ {} попал в формирование buttonTapCurrencyDelete", callData);
                    execute(buttonHandler.buttonTapCurrencyDelete(chatId, callData, messageId));
                }
                if (callData.equals(PARAM_PERIOD) || Arrays.stream(PredictPeriod.values()).map(Enum::toString).toList().toString().equalsIgnoreCase(callData)) {
                    log.debug("Ответ {} попал в формирование buttonTapDateOrPeriod", callData);
                    execute(buttonHandler.buttonTapDateOrPeriod(chatId, callData, messageId));
                }
                if (callData.equals(DATE) || NUMBERS.contains(callData) || callData.contains(DEL_INT) || callData.equals(DEL_ALL)) {
                    log.debug("Ответ {} попал в формирование buttonTapDate", callData);
                    execute(buttonHandler.buttonTapDate(chatId, callData, messageId));
                }
                if (callData.equals(PERIOD)) {
                    log.debug("Ответ {} попал в формирование buttonTapPeriod", callData);
                    execute(buttonHandler.buttonTapPeriod(chatId, callData, messageId));
                }
                if (callData.equals(ALG) || Arrays.stream(Period.values()).map(Enum::toString).toList().toString().toLowerCase().contains(callData.toLowerCase())) {
                    log.debug("Ответ {} попал в формирование buttonTapCurrencyDelete", callData);
                    execute(buttonHandler.buttonTapAlg(chatId, callData, messageId));
                }
                if (callData.equals(OUTPUT) || Arrays.stream(Output.values()).map(Enum::toString).toList().toString().toLowerCase().contains(callData.toLowerCase()) || Arrays.stream(PredictAlgorithms.values()).map(Enum::toString).toList().toString().toLowerCase().contains(callData.toLowerCase())) {
                    log.debug("Ответ {} попал в формирование buttonTapOutput", callData);
                    execute(buttonHandler.buttonTapOutput(chatId, callData, messageId));
                }
                if (callData.equals(RESULT.concat(GRAPH))) {
                    log.debug("Ответ {} совпал с {}", callData, RESULT.concat(GRAPH));
                    editMessage(chatId, messageText, messageId);
                    sendPhoto(Long.toString(chatId), buttonHandler.buttonTapResult(chatId));
                }
                if (callData.equals(RESULT) || callData.equals(RESULT.concat(LIST))) {
                    log.debug("Ответ {} совпал с {} или с {}", callData, RESULT, RESULT.concat(LIST));
                    editMessage(chatId, messageText, messageId);
                    sendText(chatId, buttonHandler.buttonTapResult(chatId));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.debug("Получили {} от {}", update.getMessage().getClass().toString(), update.getMessage().getChatId().toString());
        }
    }

    public void editMessage(Long chatId, String text, int mesId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId(toIntExact(mesId));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhoto(String who, String what) {
        if (!what.isEmpty()) {
            SendPhoto message = new SendPhoto(who, new InputFile(new File(what)));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendTextWithKb(Long who, String what, InlineKeyboardMarkup kb) {
        SendMessage message = new SendMessage();
        message.setChatId(who);
        message.setText(what);
        message.setReplyMarkup(kb);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendText(Long who, String what) {
        if (!what.isEmpty()) {
            SendMessage sm = SendMessage.builder()
                    .chatId(who.toString())
                    .text(what).build();
            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getBotUsername() {
        return "JavaEduHomework2Bot";
    }

    @Override
    public String getBotToken() {
        return "6084604584:AAGxfp2i7i4VMl_0XNx6QgxZrrpYrqzLtmU";
    }

}


