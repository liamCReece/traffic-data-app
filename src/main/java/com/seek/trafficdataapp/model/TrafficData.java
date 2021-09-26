package com.seek.trafficdataapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficData {
    private LocalDateTime dateTime;
    private int trafficCount;

    @Override
    public String toString() {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " " + trafficCount;
    }
}
