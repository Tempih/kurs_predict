package ru.liga.coursepredict.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.coursepredict.exceptions.IncorrectDateFormatException;

@Slf4j
public class Bot extends TelegramLongPollingBot {
    private static final UpdateHandler updateHandler = new UpdateHandler();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                updateHandler.handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                updateHandler.handleCallbackQuery(update.getCallbackQuery());
            } else {
                log.debug("Получили {} от {}", update.getMessage().getClass().toString(), update.getMessage().getChatId().toString());
            }
        } catch (IncorrectDateFormatException e) {
            log.debug("Ошибка в формате даты");
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


