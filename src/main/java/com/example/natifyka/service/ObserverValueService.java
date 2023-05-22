package com.example.natifyka.service;

import com.example.natifyka.bot.TelegramBot;
import com.example.natifyka.model.Paper;
import com.example.natifyka.utils.Queries;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObserverValueService extends Thread{
    private final Queries queries;
    private final PaperService paperService;
    private final TelegramBot telegramBot;

    public ObserverValueService(Queries queries, PaperService paperService, TelegramBot telegramBot) {
        this.queries = queries;
        this.paperService = paperService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void post(){
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            //todo getAllWhereSubscriberIsActive
            List<Paper> papers = paperService.getAll();
            for (Paper paper : papers) {
                int tradesCount = queries.getTradesCount(paper);
                if(tradesCount > paper.getObservedCount()){
                    telegramBot.sendMessage(paper.getSubscriber().getChatId(), "ALARM" +
                            " бумага " + paper.getSecurity() + " превысила " + paper.getObservedCount() + "!");
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
