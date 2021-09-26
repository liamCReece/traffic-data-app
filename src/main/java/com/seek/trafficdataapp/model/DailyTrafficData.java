package com.seek.trafficdataapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTrafficData {
    private LocalDate date;
    private int trafficCount;

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ISO_DATE) + " " + trafficCount;
    }
}
