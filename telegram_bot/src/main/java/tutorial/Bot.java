package tutorial;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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
        return "insert your bot token";
    }

    public void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        String response = "In process...";  // simple reply for all buttons

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();

        try {
            execute(message);

            // Also answer the callback query to stop the loading spinner
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            execute(answer);

        } catch (TelegramApiException e) {
            System.err.println("Error sending callback response: " + e.getMessage());
        }
    }



    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var msg = update.getMessage();
                long chatId = msg.getChatId();
                var user = msg.getFrom();
                System.out.println(user.getFirstName() + " " + chatId);

                String text = msg.getText();

                if (msg.hasText()) {

                    if (text.equalsIgnoreCase("/start")) {
                        sendMenuButtons(chatId);
                    } else if (text.equalsIgnoreCase("Hi")) {
                        sendText(chatId, "Hi, " + user.getFirstName());
                    } else if (text.equalsIgnoreCase("bye")) {
                        sendText(chatId, "Bye, " + user.getFirstName());
                    } else if (text.equalsIgnoreCase("about")) {
                        sendText(chatId, "I am your personal Vault for your favourite reels, and i'll help you summarize, extract audio, and do all sorta things with your reels." +
                                "Just send them over to me 😉.");
                    } else if (text.equalsIgnoreCase("help")) {
                        sendText(chatId, "To use this bot, press one of the inline buttons or type commands like /start, about, help.");
                    } else if (msg.hasPhoto()) {
                        sendText(chatId, "Cool picture! " + user.getFirstName());
                    } else if (msg.hasSticker()) {
                        sendText(chatId, "Cool sticker! " + user.getFirstName());
                    }
                }

            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
        }
    }

    public void sendKeyboardButtons(long chatId) {
        KeyboardButton about = new KeyboardButton("about");
        KeyboardButton help = new KeyboardButton("help");

        KeyboardRow row = new KeyboardRow();
        row.add(about);
        row.add(help);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Choose an option:")
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending keyboard buttons: " + e.getMessage());
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
        InlineKeyboardButton btn1 = new InlineKeyboardButton("🔊 Extract Audio");
        btn1.setCallbackData("extract_audio");

        InlineKeyboardButton btn2 = new InlineKeyboardButton("🧠 Summarize Reel");
        btn2.setCallbackData("summarize_reel");

        InlineKeyboardButton btn3 = new InlineKeyboardButton("📂 Save to Folder");
        btn3.setCallbackData("save_reel");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(btn1);
        row.add(btn2);
        row.add(btn3);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("🎉 Welcome to ReelVault! What would you like to do?")
                .replyMarkup(markup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending inline keyboard: " + e.getMessage());
        }
    }

}


