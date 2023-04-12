package ru.liga.coursepredict.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

import static java.lang.Math.toIntExact;

public class Sender {
    public EditMessageText editMessage(Long chatId, String text, int mesId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId(toIntExact(mesId));
        return message;
    }

    public SendPhoto sendPhoto(String who, String what) {
        SendPhoto message = new SendPhoto(who, new InputFile(new File(what)));
        return message;
    }

    public SendMessage sendTextWithKb(Long who, String what, InlineKeyboardMarkup kb) {
        SendMessage message = new SendMessage();
        message.setChatId(who);
        message.setText(what);
        message.setReplyMarkup(kb);
        return message;
    }

    public SendMessage sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        return sm;
    }
}
