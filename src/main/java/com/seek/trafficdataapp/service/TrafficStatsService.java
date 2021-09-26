package com.seek.trafficdataapp.service;

import com.seek.trafficdataapp.model.DailyTrafficData;
import com.seek.trafficdataapp.model.TrafficData;
import com.seek.trafficdataapp.model.TrafficDataForTimePeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrafficStatsService {
    public int getTotalVehiclesSeen(List<TrafficData> input) {
        return input.stream().map(TrafficData::getTrafficCount).mapToInt(Integer::intValue).sum();
    }

    public List<TrafficData> getTopBusiestPeriods(List<TrafficData> input, int topX) {
        List<TrafficData> sortedByBusiest = input.stream().sorted(Comparator.comparing(TrafficData::getTrafficCount).reversed())
                .collect(Collectors.toList());

        if (sortedByBusiest.size() <= topX) {
            return sortedByBusiest;
        }

        return sortedByBusiest.stream().limit(topX).collect(Collectors.toList());
    }

    public List<DailyTrafficData> getDailyTrafficData(List<TrafficData> input) {
        //partition traffic data by date
        Map<LocalDate, List<TrafficData>> trafficByDate
                = input.stream().collect(Collectors.groupingBy(trafficData -> trafficData.getDateTime().toLocalDate()));


        return trafficByDate.entrySet().stream().map(entry -> {
                    LocalDate date = entry.getKey();
                    int totalTrafficForDate = entry.getValue().stream()
                            .map(TrafficData::getTrafficCount).mapToInt(Integer::intValue).sum();
                    return DailyTrafficData.builder().date(date).trafficCount(totalTrafficForDate).build();
                }).sorted(Comparator.comparing(DailyTrafficData::getDate))
                .collect(Collectors.toList());

    }

    /**
     * @param input              already sorted from earliest to latest
     * @param numberOfDataPoints eg 3 for 1.5 hours, 4 for 2 hours, 5 for 2.5 hours
     * @return List is returned for multiple time periods with the same lowest traffic count
     */
    public List<TrafficDataForTimePeriod> getTimePeriodsWithLeastTraffic(List<TrafficData> input, int numberOfDataPoints) {
        List<TrafficDataForTimePeriod> result = new ArrayList<>();
        int lowestTrafficForTimePeriod = -1;
        int currentIndex = 0;

        for (TrafficData currentDataPoint : input) {
            if (currentIndex + numberOfDataPoints <= input.size()) {
                ArrayList<TrafficData> dataPoints = new ArrayList<>();
                dataPoints.add(currentDataPoint);
                for (int i = 1; i < numberOfDataPoints; i++) {
                    dataPoints.add(input.get(currentIndex + i));
                }
                Optional<TrafficDataForTimePeriod> optionalTrafficDataForTimePeriod = consolidateDataPointsForTimePeriod(dataPoints);

                if (optionalTrafficDataForTimePeriod.isPresent()) {
                    TrafficDataForTimePeriod trafficDataForTimePeriod = optionalTrafficDataForTimePeriod.get();
                    if (lowestTrafficForTimePeriod == -1 || lowestTrafficForTimePeriod == trafficDataForTimePeriod.getTrafficCount()) {
                        result.add(trafficDataForTimePeriod);
                        lowestTrafficForTimePeriod = trafficDataForTimePeriod.getTrafficCount();
                    } else if (lowestTrafficForTimePeriod > trafficDataForTimePeriod.getTrafficCount()) {
                        result.clear();
                        result.add(trafficDataForTimePeriod);

                        lowestTrafficForTimePeriod = trafficDataForTimePeriod.getTrafficCount();
                    }

                }
            }
            currentIndex++;
        }

        return result;
    }

    /**
     * @param input
     * @return empty if time gap between data points is not 30mins
     */
    private Optional<TrafficDataForTimePeriod> consolidateDataPointsForTimePeriod(List<TrafficData> input) {
        for (int i = 0; i < input.size() - 1; i++) {
            if (dataPointsMissingInTimePeriod(input.get(i).getDateTime(), input.get(i + 1).getDateTime())) {
                log.debug("gap between two data points is not 30mins, {}->{}",
                        input.get(i).getDateTime(), input.get(i + 1).getDateTime());

                return Optional.empty();
            }
        }

        return Optional.of(TrafficDataForTimePeriod.builder()
                .start(input.get(0).getDateTime())
                .end(input.get(input.size() - 1).getDateTime().plusMinutes(30L))
                .trafficCount(getTotalVehiclesSeen(input))
                .build());
    }

    private boolean dataPointsMissingInTimePeriod(LocalDateTime start, LocalDateTime finish) {
        return ChronoUnit.MINUTES.between(start, finish) != 30L;
    }

}
