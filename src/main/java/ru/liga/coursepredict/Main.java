package ru.liga.coursepredict;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.liga.coursepredict.telegram.Bot;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws TelegramApiException {
        logger.debug("Приложение запускается");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        logger.debug("Регистрируем тг бота");
        botsApi.registerBot(bot);
        logger.debug("Зарегистрировали тг бота");

    }

}

