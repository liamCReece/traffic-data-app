package com.seek.trafficdataapp.service;

import com.seek.trafficdataapp.model.TrafficData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InputDataToTrafficDataConvertor {

    /**
     * @param fileContent
     * @return List of traffic data sorted by datetime
     */
    public List<TrafficData> getTrafficDataFromFileContent(String fileContent) {
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        List<String> lines = Arrays.asList(fileContent.split(System.lineSeparator()));
        return lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .map(line -> {
                    String[] splits = line.split(" ");
                    LocalDateTime dateTime = LocalDateTime.parse(splits[0], df);
                    int count = Integer.parseInt(splits[1]);

                    return TrafficData.builder().dateTime(dateTime).trafficCount(count).build();
                }).sorted(Comparator.comparing(TrafficData::getDateTime))
                .collect(Collectors.toList());
    }
}
