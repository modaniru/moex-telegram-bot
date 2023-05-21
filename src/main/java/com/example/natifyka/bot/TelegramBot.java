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
        if(update.hasMessage() && update.getMessage().hasText()){
            Long userId = update.getMessage().getFrom().getId();
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(!subscriberService.userExists(userId) && !messageText.equals("/start")){
                sendMessage(chatId, "Эй! Сначала подпишись на бота '/start'");
                return;
            }
            if(messageText.startsWith("/add")){
                if (paperService.savePaper(messageText, userId)) {
                    sendMessage(chatId, "Бумага добавлена");
                }
                else{
                    sendMessage(chatId, "Ошибка");
                }
                return;
            }
            if(messageText.startsWith("/delete")){
                if (paperService.deletePaper(messageText, userId)) {
                    sendMessage(chatId, "Бумага удалена");
                }
                else{
                    sendMessage(chatId, "Ошибка");
                }
                return;
            }
            switch (messageText){
                case "/start":
                    if (subscriberService.saveEmptySubscriber(update.getMessage().getFrom().getId())) {
                        sendMessage(chatId, "Вы подписались!\nВведите команду /help.");
                        break;
                    }
                    sendMessage(chatId, "Ошибка");
                    break;
                case "/follow":
                    subscriberService.setTrueActive(userId);
                    sendMessage(chatId, "Теперь будем присылать тебе уведомления");
                    break;
                case "/unfollow":
                    subscriberService.setFalseActive(userId);
                    sendMessage(chatId, "Теперь не будем присылать тебе уведомления");
                    break;
                case "/help":
                    sendMessage(chatId, "/follow - включить уведомления\n" +
                            "/unfollow - выключить уведомления\n" +
                            "/add [engine] [market] [boardGroups] [security] [date] [coefficient]\n" +
                            "\t[date] -> с какой даты сравнивать\n" +
                            "\t[coefficient] -> с каким коэфом сравнивать");
                    break;
                case "/all":
                    List<Paper> allPapersById = paperService.getAllPapersById(userId);
                    if(allPapersById.isEmpty()){
                        sendMessage(chatId, "Нет отслеживаемых бумаг!");
                        return;
                    }
                    StringBuilder res = new StringBuilder();
                    for (Paper paper : allPapersById) {
                        res.append(paper.getDescription());
                    }
                    sendMessage(chatId, res.toString());
                    break;

                default:
                    sendMessage(chatId, "message");
            }
        }

    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
        }
    }
}
