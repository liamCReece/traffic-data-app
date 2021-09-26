package com.seek.trafficdataapp.cli;


import com.seek.trafficdataapp.file.FileManager;
import com.seek.trafficdataapp.model.DailyTrafficData;
import com.seek.trafficdataapp.model.TrafficData;
import com.seek.trafficdataapp.model.TrafficDataForTimePeriod;
import com.seek.trafficdataapp.service.InputDataToTrafficDataConvertor;
import com.seek.trafficdataapp.service.TrafficStatsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TrafficDataCommandLineRunner implements CommandLineRunner {

    private final FileManager fileManager;
    private final InputDataToTrafficDataConvertor inputDataToTrafficDataConvertor;
    private final TrafficStatsService trafficStatsService;

    @Autowired
    public TrafficDataCommandLineRunner(FileManager fileManager,
                                        InputDataToTrafficDataConvertor inputDataToTrafficDataConvertor,
                                        TrafficStatsService trafficStatsService) {

        this.fileManager = fileManager;
        this.inputDataToTrafficDataConvertor = inputDataToTrafficDataConvertor;
        this.trafficStatsService = trafficStatsService;
    }

    @Override
    public void run(String... args) {
        if (args.length != 1
                || StringUtils.isEmpty(args[0])) {
            log.error("Usage: <traffic_data.txt>");
            return;
        }
        String fileName = args[0];
        try {
            log.info("open file {}", fileName);
            String inputFileContent = fileManager.fileToString(fileName);
            List<TrafficData> inputTrafficData = inputDataToTrafficDataConvertor.getTrafficDataFromFileContent(inputFileContent);

            int totalVehicles = trafficStatsService.getTotalVehiclesSeen(inputTrafficData);

            List<TrafficData> top3 = trafficStatsService.getTopBusiestPeriods(inputTrafficData, 3);

            List<DailyTrafficData> dailyTrafficStatus = trafficStatsService.getDailyTrafficData(inputTrafficData);

            List<TrafficDataForTimePeriod> timePeriodsWithLeastTraffic = trafficStatsService.getTimePeriodsWithLeastTraffic(inputTrafficData, 3);

            outputTopTotalVehicles(totalVehicles);
            outputTop3File(top3);
            outputToDailyFile(dailyTrafficStatus);
            outputToLeastCarsFile(timePeriodsWithLeastTraffic);

        } catch (IOException exception) {
            log.error("error open/marshal/save file {}", exception.getMessage());
        }

    }

    private void outputTopTotalVehicles(Integer total) throws IOException {
        fileManager.stringToFile(total.toString(), "1_total_vehicles.txt");
        log.info("1_total_vehicles.txt generated");
    }

    private void outputTop3File(List<TrafficData> top3) throws IOException {
        fileManager.stringToFile(
                top3.stream().map(TrafficData::toString).collect(Collectors.joining(System.lineSeparator())),
                "2_output_top3.txt");
        log.info("2_output_top3.txt generated");
    }

    private void outputToDailyFile(List<DailyTrafficData> dailyTrafficStatus) throws IOException {
        fileManager.stringToFile(
                dailyTrafficStatus.stream().map(DailyTrafficData::toString).collect(Collectors.joining(System.lineSeparator())),
                "3_output_daily_status.txt");
        log.info("3_output_daily_status.txt generated");
    }

    private void outputToLeastCarsFile(List<TrafficDataForTimePeriod> trafficDataForTimePeriods) throws IOException {
        fileManager.stringToFile(
                trafficDataForTimePeriods.stream().map(TrafficDataForTimePeriod::toString).collect(Collectors.joining(System.lineSeparator())),
                "4_output_periods_with_least_traffic.txt");
        log.info("4_output_periods_with_least_traffic.txt generated");
    }

}
