package com.seek.trafficdataapp.cli;

import com.seek.trafficdataapp.file.FileManager;
import com.seek.trafficdataapp.model.DailyTrafficData;
import com.seek.trafficdataapp.model.TrafficData;
import com.seek.trafficdataapp.model.TrafficDataForTimePeriod;
import com.seek.trafficdataapp.service.InputDataToTrafficDataConvertor;
import com.seek.trafficdataapp.service.TrafficStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TrafficDataCommandLineRunnerUnitTest {

    @Mock
    private FileManager fileManager;

    @Mock
    private InputDataToTrafficDataConvertor inputDataToTrafficDataConvertor;

    @Mock
    private TrafficStatsService trafficStatsService;

    private TrafficDataCommandLineRunner testInstance;

    @BeforeEach
    void setup() {
        testInstance = new TrafficDataCommandLineRunner(fileManager, inputDataToTrafficDataConvertor, trafficStatsService);
    }

    @Test
    void run_shouldNotProceed_withNoCliArgument() throws IOException {
        testInstance.run();

        noInvoke();
    }

    @Test
    void run_shouldNotProceed_withWrongNumberOfCliArgument() throws IOException {
        testInstance.run("file1", "file2");

        noInvoke();
    }

    @Test
    void run_shouldProceed_withCorrectNumberOfCliArgument() throws IOException {
        when(fileManager.fileToString(anyString())).thenReturn("content");
        when(inputDataToTrafficDataConvertor.getTrafficDataFromFileContent(anyString())).thenReturn(new ArrayList<>());
        when(trafficStatsService.getTotalVehiclesSeen(anyList())).thenReturn(5);
        when(trafficStatsService.getTopBusiestPeriods(anyList(), anyInt()))
                .thenReturn(List.of(TrafficData.builder()
                        .dateTime(LocalDateTime.now()).trafficCount(2).build()));
        when(trafficStatsService.getDailyTrafficData(anyList()))
                .thenReturn(List.of(DailyTrafficData.builder().date(LocalDate.now()).trafficCount(3).build()));
        when(trafficStatsService.getTimePeriodsWithLeastTraffic(anyList(), anyInt()))
                .thenReturn(List.of(TrafficDataForTimePeriod.builder()
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusMinutes(90L))
                        .trafficCount(15)
                        .build()));

        doNothing().when(fileManager).stringToFile(anyString(), anyString());

        testInstance.run("file1");

        verify(fileManager, times(1)).fileToString("file1");
        verify(inputDataToTrafficDataConvertor, times(1)).getTrafficDataFromFileContent("content");
        verify(trafficStatsService, times(1)).getTotalVehiclesSeen(anyList());
        verify(trafficStatsService, times(1)).getTopBusiestPeriods(anyList(), anyInt());
        verify(trafficStatsService, times(1)).getDailyTrafficData(anyList());
        verify(trafficStatsService, times(1)).getTimePeriodsWithLeastTraffic(anyList(), anyInt());

        //output 4 files
        verify(fileManager, times(4)).stringToFile(anyString(), anyString());
    }

    private void noInvoke() throws IOException {
        verify(fileManager, times(0)).fileToString(anyString());
        verify(inputDataToTrafficDataConvertor, times(0)).getTrafficDataFromFileContent(anyString());
        verify(trafficStatsService, times(0)).getTotalVehiclesSeen(anyList());
        verify(trafficStatsService, times(0)).getTopBusiestPeriods(anyList(), anyInt());
        verify(trafficStatsService, times(0)).getDailyTrafficData(anyList());
        verify(trafficStatsService, times(0)).getTimePeriodsWithLeastTraffic(anyList(), anyInt());
        verify(fileManager, times(0)).stringToFile(anyString(), anyString());
    }
}