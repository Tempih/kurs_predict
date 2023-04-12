package ru.liga.coursepredict;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.liga.coursepredict.telegram.Bot;
@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramApiException {
        log.debug("Приложение запускается");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        log.debug("Регистрируем тг бота");
        botsApi.registerBot(bot);
        log.debug("Зарегистрировали тг бота");
    }

}

