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

    public Integer getYesterdayAvgTrades(Paper paper, LocalDate date) throws JsonProcessingException {
        String getTrades =
                "https://iss.moex.com/iss/engines/" + paper.getEngine() +
                        "/markets/" + paper.getMarket() +
                        "/boardgroups/" + paper.getBoardGroups() +
                        "/securities/" + paper.getSecurity() +
                        "/candles.jsonp?from=" + date + "&interval=" + 1 + "&till=" + date;
        JsonNode root = getRootNode(getTrades, "candles");
        int index = getIndexFromColumn(root, "volume");
        ArrayList<ArrayList<Object>> arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
        int sum = arr.stream().mapToInt(a -> (int) a.get(index)).sum();
        return sum / arr.size();
    }

    public int getTradesCount(Paper paper) throws JsonProcessingException {
        String getTrade =
                "https://iss.moex.com/iss/engines/" + paper.getEngine() +
                        "/markets/" + paper.getMarket() +
                        "/boardgroups/" + paper.getBoardGroups() +
                        "/securities/" + paper.getSecurity() +
                        ".jsonp";
        JsonNode root = getRootNode(getTrade, "marketdata");
        int index = getIndexFromColumn(root, "NUMTRADES");
        ArrayList<ArrayList<Object>> arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
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