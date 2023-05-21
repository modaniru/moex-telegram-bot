package com.example.natifyka.service;

import com.example.natifyka.model.Paper;
import com.example.natifyka.model.Subscriber;
import com.example.natifyka.repository.PaperRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaperService {
    private final PaperRepository paperRepository;
    private final SubscriberService subscriberService;

    @Autowired
    public PaperService(PaperRepository paperRepository, SubscriberService subscriberService) {
        this.paperRepository = paperRepository;
        this.subscriberService = subscriberService;
    }

    public boolean savePaper(String text, Long subscriberId){
        String[] args = text.split(" ");
        if(args.length != 7) return false;
        double coeff = 0;
        try {
            coeff = Double.parseDouble(args[6]);
        } catch (NumberFormatException e) {
            return false;
        }
        Paper paper = Paper.builder()
                .engine(args[1])
                .market(args[2])
                .boardGroups(args[3])
                .security(args[4])
                .observedCount(0L)//todo
                .coefficient(coeff).build();
        paper.setSubscriber(subscriberService.getById(subscriberId));
        paperRepository.save(paper);
        return true;
    }

    @Transactional
    public boolean deletePaper(String text, Long subscriberId){
        String[] args = text.split(" ");
        if(args.length != 2) return false;
        try {
            paperRepository.deleteBySubscriberIdAndId(subscriberId, Long.parseLong(args[1]));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public List<Paper> getAllPapersById(Long id){
        return paperRepository.findAllBySubscriberId(id);
    }
}
