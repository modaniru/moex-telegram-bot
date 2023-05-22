package com.example.natifyka.bot;

import com.example.natifyka.config.BotConfiguration;
import com.example.natifyka.model.Paper;
import com.example.natifyka.service.PaperService;
import com.example.natifyka.service.SubscriberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfiguration botConfig;
    private final SubscriberService subscriberService;
    private final PaperService paperService;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getSecret();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long userId = update.getMessage().getFrom().getId();
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (!subscriberService.userExists(userId) && !messageText.equals("/start")) {
                sendMessage(chatId, "\uD83D\uDE21 Сначала подпишись на бота /start. ");
                return;
            }
            else if (messageText.equals("/start")){
                subscriberService.saveEmptySubscriber(userId, chatId);
                sendMessage(chatId, "\uD83D\uDD25 Привет! Напиши /help для подробностей. \uD83D\uDCC9");
                return;
            }
            if (messageText.startsWith("/add")) {
                if (paperService.savePaper(messageText, userId)) {
                    sendMessage(chatId, "\uD83D\uDCCC Бумага добавлена.");
                } else {
                    sendMessage(chatId, "\uD83D\uDE14 Ошибка при добавлении этой бумаги.");
                }
                return;
            }
            if (messageText.startsWith("/delete")) {
                if (paperService.deletePaper(messageText, userId)) {
                    sendMessage(chatId, "\uD83D\uDDD1 Бумага удалена.");
                } else {
                    sendMessage(chatId, "\uD83D\uDE14 Ошибка при удалении бумаги.");
                }
                return;
            }
            switch (messageText) {
                case "/follow":
                    subscriberService.setTrueActive(userId);
                    sendMessage(chatId, "✔\uFE0F Теперь будем присылать тебе уведомления.");
                    break;
                case "/unfollow":
                    subscriberService.setFalseActive(userId);
                    sendMessage(chatId, "\uD83D\uDCA4 Теперь не будем присылать тебе уведомления.");
                    break;
                case "/help":
                    sendMessage(chatId, """
                            /follow - включить уведомления
                            /unfollow - выключить уведомления
                            /add [engine] [market] [boardId] [security] [date] [coefficient]\s
                             [date] -> с какой датой сравнивать
                             [coefficient] -> коэффициент оповещения
                            Пример: /add stock shares 57 SBER 2023-04-12 1
                            /all - вывести все наблюдаемые бумаги
                            /delete [id] - удалить бумагу по идентификатору
                            /info - терминология""");
                    break;
                case "/info":
                    sendMessage(chatId, """
                            engine - механизм торговли
                            market - рынок
                            board - идентификатор режима торгов
                            security - статические данные по бумаге""");
                    break;
                case "/all":
                    List<Paper> allPapersById = paperService.getAllPapersById(userId);
                    if (allPapersById.isEmpty()) {
                        sendMessage(chatId, "\uD83D\uDE14 Нет отслеживаемых бумаг!");
                        return;
                    }
                    StringBuilder res = new StringBuilder();
                    for (Paper paper : allPapersById) {
                        res.append(paper.getDescription());
                    }
                    sendMessage(chatId, res.toString());
                    break;

                default:
                    sendMessage(chatId, "\uD83D\uDE14 Такого я не умею");
            }
        }

    }

    public void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
        }
    }
}
