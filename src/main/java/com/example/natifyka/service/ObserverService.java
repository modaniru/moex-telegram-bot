package com.example.natifyka.service;

import com.example.natifyka.bot.TelegramBot;
import com.example.natifyka.model.Paper;
import com.example.natifyka.utils.Queries;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ObserverService extends Thread {
    private final Queries queries;
    private final PaperService paperService;
    private final TelegramBot telegramBot;

    public ObserverService(Queries queries, PaperService paperService, TelegramBot telegramBot) {
        this.queries = queries;
        this.paperService = paperService;
        this.telegramBot = telegramBot;
    }

    //starting the observing thread
    @PostConstruct
    public void post() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            List<Paper> papers = paperService.getAllPaperWithActiveUser();
            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
            if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                for (Paper paper : papers) {
                    int tradesCount = queries.getTradesCount(paper);
                    if (tradesCount > paper.getObservedCount()) {
                        telegramBot.sendMessage(paper.getSubscriber().getChatId(), "❗\uFE0F❗\uFE0F❗\uFE0F\n" +
                                "Бумага: " + paper.getSecurity() + " (" + paper.getId() + ")\n" +
                                "Превысила объем сделок (" + paper.getObservedCount() + ") -> " + tradesCount + "\n" +
                                "❗\uFE0F❗\uFE0F❗\uFE0F");
                    }
                }
            }
            try {
                //interval equals 1 minute
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
