package com.example.natifyka.service;

import com.example.natifyka.model.Subscriber;
import com.example.natifyka.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public Subscriber getById(Long id){
        return subscriberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public void saveSubscriber(Subscriber subscriber){
        subscriberRepository.save(subscriber);
    }

    public boolean saveEmptySubscriber(Long id, Long chatId){
        if (subscriberRepository.existsById(id)){
            return false;
        }
        Subscriber sub = Subscriber.builder().id(id).chatId(chatId).isActive(false).build();
        subscriberRepository.save(sub);
        return true;
    }

    public boolean userExists(Long id){
        return subscriberRepository.existsById(id);
    }

    public void setTrueActive(Long id){
        Subscriber subscriber = subscriberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        subscriber.setActive(true);
        subscriberRepository.save(subscriber);
    }

    public void setFalseActive(Long id){
        Subscriber subscriber = subscriberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        subscriber.setActive(false);
        subscriberRepository.save(subscriber);
    }
}
