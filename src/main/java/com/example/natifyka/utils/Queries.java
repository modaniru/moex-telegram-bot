package com.example.natifyka.utils;

import com.example.natifyka.model.Paper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class Queries {
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    public Integer getYesterdayAvgTrades(Paper paper, LocalDate date){
        String getTrades =
                "https://iss.moex.com/iss/engines/" + paper.getEngine() +
                        "/markets/" + paper.getMarket() +
                        "/boardgroups/" + paper.getBoardGroups() +
                        "/securities/" + paper.getSecurity() +
                        "/candles.jsonp?from=" + date + "&interval=" + 1 + "&till=" + date;

        JsonNode root = null;
        final int index;
        ArrayList<ArrayList<Object>> arr = null;
        try {
            root = getRootNode(getTrades, "candles");
            index = getIndexFromColumn(root, "volume");
            arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        int sum = arr.stream().mapToInt(a -> (int) a.get(index)).sum();
        return sum / arr.size();
    }

    public int getTradesCount(Paper paper) {
        String getTrade =
                "https://iss.moex.com/iss/engines/" + paper.getEngine() +
                        "/markets/" + paper.getMarket() +
                        "/boardgroups/" + paper.getBoardGroups() +
                        "/securities/" + paper.getSecurity() +
                        ".jsonp";
        JsonNode root = null;
        final int index;
        ArrayList<ArrayList<Object>> arr = null;
        try {
            root = getRootNode(getTrade, "marketdata");
            index = getIndexFromColumn(root, "NUMTRADES");
            arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        int trades = arr.stream().map(a -> (int) a.get(index)).toList().get(0);
        return trades;
    }

    private int getIndexFromColumn(JsonNode root, String name) throws JsonProcessingException {
        ArrayList<String> arrayList = objectMapper.readValue(root.get("columns").toString(), ArrayList.class);
        return arrayList.indexOf(name);
    }

    private JsonNode getRootNode(String uri, String root) throws JsonProcessingException {
        ResponseEntity<String> entity = restTemplate.getForEntity(uri, String.class);
        JsonNode main = objectMapper.readTree(entity.getBody());
        return main.get(root);
    }
}