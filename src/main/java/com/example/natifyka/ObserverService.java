package com.example.natifyka;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ObserverService {
    @PostConstruct
    public void init(){
        startThread();
    }

    private void startThread(){

    }
}
