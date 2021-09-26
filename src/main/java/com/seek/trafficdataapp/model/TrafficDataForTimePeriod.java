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
public class TrafficDataForTimePeriod {
    private LocalDateTime start;
    private LocalDateTime end;
    private int trafficCount;

    @Override
    public String toString() {
        return start.format(DateTimeFormatter.ISO_DATE_TIME) + " "
                + end.format(DateTimeFormatter.ISO_DATE_TIME) + " "
                + trafficCount;
    }
}
