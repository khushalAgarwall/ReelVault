package tutorial;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private final Map<Long, String> userReelLinks = new HashMap<>(); // user ID -> reel URL

    @Override
    public String getBotUsername() {
        return "Reel_Vault_bot";
    }

    @Override
    public String getBotToken() {
        return "7789555374:AAGRkNhmfvQCEQz1564idZhuJzmbwgU8l0o";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var msg = update.getMessage();
                long chatId = msg.getChatId();
                var user = msg.getFrom();
                String text = msg.getText();

                System.out.println(user.getFirstName() + " " + chatId);

                if (text.contains("instagram.com/reel/")) {
                    userReelLinks.put(chatId, text); // Save the reel link for this user
                    sendMenuButtons(chatId);         // Show options
                    return;
                }

                switch (text.toLowerCase()) {
                    case "/start":
                        sendMenuButtons(chatId);
                        break;
                    case "hi":
                        sendText(chatId, "Hi, " + user.getFirstName());
                        break;
                    case "bye":
                        sendText(chatId, "Bye, " + user.getFirstName());
                        break;
                    case "about":
                        sendText(chatId, "I am your personal Vault for your favourite reels. I can summarize, extract audio, and save them. Just send a reel link 😉.");
                        break;
                    case "help":
                        sendText(chatId, "To use this bot, send an Instagram reel link and select what you want to do from the menu.");
                        break;
                    default:
                        sendText(chatId, "Send me an Instagram reel link to get started!");
                        break;
                }

            } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
                sendText(update.getMessage().getChatId(), "Cool picture! " + update.getMessage().getFrom().getFirstName());
            } else if (update.hasMessage() && update.getMessage().hasSticker()) {
                sendText(update.getMessage().getChatId(), "Cool sticker! " + update.getMessage().getFrom().getFirstName());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
        }
    }

    public void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        String response = switch (data) {
            case "extract_audio" -> {
                String reel = userReelLinks.getOrDefault(chatId, "no reel found");
                yield "🔊 Extracting audio from:\n" + reel;
            }
            case "summarize_reel" -> {
                String reel = userReelLinks.getOrDefault(chatId, "no reel found");
                yield "🧠 Summarizing this reel:\n" + reel;
            }
            case "save_reel" -> {
                String reel = userReelLinks.getOrDefault(chatId, "no reel found");
                yield "📂 Saving this reel:\n" + reel;
            }
            default -> "⚠️ Unknown action.";
        };

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(response)
                .build();

        try {
            execute(message);

            // Stop the loading spinner
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .build());
        } catch (TelegramApiException e) {
            System.err.println("Error sending callback response: " + e.getMessage());
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
                .text("🎉 What would you like to do with this?")
                .replyMarkup(markup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending inline keyboard: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            botsApi.registerBot(bot);
            System.out.println("Bot started successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error starting bot: " + e.getMessage());
        }
    }
}
