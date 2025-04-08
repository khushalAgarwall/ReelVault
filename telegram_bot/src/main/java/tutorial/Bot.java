package tutorial;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;


// importing all dependencies


public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Reel_Vault_bot";
        // @override function used to tweak with default functions and method
    }

    @Override
    public String getBotToken() {
        return "7789555374:AAGRkNhmfvQCEQz1564idZhuJzmbwgU8l0o";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            long chatId = update.getMessage().getChatId();
            var msg = update.getMessage();
            var user = msg.getFrom();
            System.out.println(user.getFirstName() + " " + chatId);
            // Logs user's info and prints in terminal

            // Respond to user messages
            if (msg.hasText()) {
                String text = msg.getText();

                if (text.equalsIgnoreCase("/start")) {
                    sendMenuButtons(chatId);
                } else if (text.equalsIgnoreCase("Hi")) {
                    sendText(chatId, "Hi, " + user.getFirstName());
                } else if (text.equalsIgnoreCase("bye")) {
                    sendText(chatId, "Bye, " + user.getFirstName());
                } else if (msg.hasPhoto()) {
                    sendText(chatId, "Cool picture! " + user.getFirstName());
                } else if (msg.hasSticker()) {
                    sendText(chatId, "Cool sticker! " + user.getFirstName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
        }
    }

    public void sendText(long who, String what) { 
        SendMessage sm = SendMessage.builder()
                .chatId(String.valueOf(who))
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            // log and catch errors so that the bot doesn't fail
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            botsApi.registerBot(bot);
            System.out.println("Bot started successfully!");

            // NOTE: Removed the direct message send from here
            // Let the bot respond to incoming messages instead
        } catch (TelegramApiException e) {
            System.err.println("Error starting bot: " + e.getMessage());
        }
    }

    public void sendMenuButtons(long chatId) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("About"));
        row.add(new KeyboardButton("Help"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true); // Optional, makes buttons fit screen better
        keyboardMarkup.setOneTimeKeyboard(false);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Choose an option below:")
                .replyMarkup(keyboardMarkup)
                .build();

        try{
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error starting Keyboard! " + e.getMessage());
        }
    }
}
