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

    public Integer getYesterdayAvgTrades(Paper paper, LocalDate date) {
        String uri = new StringBuilder("https://iss.moex.com/iss/engines/")
                .append(paper.getEngine())
                .append("/markets/").append(paper.getMarket())
                .append("/boardgroups/").append(paper.getBoardGroups())
                .append("/securities/").append(paper.getSecurity())
                .append("/candles.jsonp?from=").append(date)
                .append("&interval=1&till=").append(date).toString();
        JsonNode root = null;
        final int index;
        ArrayList<ArrayList<Object>> arr = null;
        try {
            root = getRootNode(uri, "candles");
            index = getIndexFromColumn(root, "volume");
            arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
        } catch (Exception e) {
            return -1;
        }
        int sum = arr.stream().mapToInt(a -> (int) a.get(index)).sum();
        if(arr.size() == 0) return -1;
        return sum / arr.size();
    }

    public int getTradesCount(Paper paper) {
        String uri = new StringBuilder("https://iss.moex.com/iss/engines/")
                .append(paper.getEngine())
                .append("/markets/").append(paper.getMarket())
                .append("/boardgroups/").append(paper.getBoardGroups())
                .append("/securities/").append(paper.getSecurity())
                .append(".jsonp").toString();
        JsonNode root = null;
        final int index;
        ArrayList<ArrayList<Object>> arr = null;
        try {
            root = getRootNode(uri, "marketdata");
            index = getIndexFromColumn(root, "NUMTRADES");
            arr = objectMapper.readValue(root.get("data").toString(), ArrayList.class);
        } catch (Exception e) {
            return -1;
        }
        return arr.stream().map(a -> (int) a.get(index)).toList().get(0);
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