package com.seek.trafficdataapp.service;

import com.seek.trafficdataapp.model.DailyTrafficData;
import com.seek.trafficdataapp.model.TrafficData;
import com.seek.trafficdataapp.model.TrafficDataForTimePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrafficStatsServiceUnitTest {
    private static final LocalDateTime START_TIME = LocalDateTime.of(2000, 12, 12, 12, 0, 0, 0);

    private TrafficStatsService testInstance;

    @BeforeEach
    void setTestInstance() {
        testInstance = new TrafficStatsService();
    }

    @Test
    void getTotalVehiclesSeen_shouldGetTotal() {
        int result = testInstance.getTotalVehiclesSeen(generateTrafficData());
        assertThat(result).isEqualTo(128);
    }

    @Test
    void getTopBusiestPeriods_shouldWorkForTop3() {
        List<TrafficData> result = testInstance.getTopBusiestPeriods(generateTrafficData(), 3);
        assertThat(result).isNotNull().hasSize(3);

        assertThat(result.get(0).getDateTime()).isEqualTo(START_TIME.plusMinutes(120));
        assertThat(result.get(0).getTrafficCount()).isEqualTo(99);

        assertThat(result.get(1).getDateTime()).isEqualTo(START_TIME.plusMinutes(30));
        assertThat(result.get(1).getTrafficCount()).isEqualTo(23);

        assertThat(result.get(2).getDateTime()).isEqualTo(START_TIME);
        assertThat(result.get(2).getTrafficCount()).isEqualTo(5);
    }

    @Test
    void getTopBusiestPeriods_shouldWork_whenTopMoreThanTotalInputSize() {
        List<TrafficData> result = testInstance.getTopBusiestPeriods(generateTrafficData(), 100);
        assertThat(result).isNotNull().hasSize(5);
    }

    @Test
    void getTopBusiestPeriods_shouldWork_whenTopEqualsTotalInputSize() {
        List<TrafficData> result = testInstance.getTopBusiestPeriods(generateTrafficData(), 5);
        assertThat(result).isNotNull().hasSize(5);
    }

    @Test
    void getDailyTrafficData_shouldProviderDailyData() {
        List<TrafficData> input = new ArrayList<>();
        input.addAll(generateTrafficData());
        input.addAll(generateTrafficDataDay2());

        List<DailyTrafficData> result = testInstance.getDailyTrafficData(input);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getDate()).isEqualTo("2000-12-12");
        assertThat(result.get(0).getTrafficCount()).isEqualTo(128);

        assertThat(result.get(1).getDate()).isEqualTo("2000-12-13");
        assertThat(result.get(1).getTrafficCount()).isEqualTo(139);
    }

    @Test
    void getTimePeriodsWithLeastTraffic_shouldFindPeriodWithLeastTraffic() {
        List<TrafficData> input = new ArrayList<>();
        input.addAll(generateTrafficData());
        input.addAll(generateTrafficDataDay2());

        List<TrafficDataForTimePeriod> result = testInstance.getTimePeriodsWithLeastTraffic(input, 3);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getStart()).isEqualTo(START_TIME.plusMinutes(30));
        assertThat(result.get(0).getEnd()).isEqualTo(START_TIME.plusMinutes(120));
        assertThat(result.get(0).getTrafficCount()).isEqualTo(24);
    }

    @Test
    void getTimePeriodsWithLeastTraffic_shouldFindPeriodsWithLeastTraffic_whenEqualLowestExist() {
        List<TrafficData> input = new ArrayList<>();
        input.addAll(generateTrafficData());
        input.addAll(generateTrafficDataDay2());
        input.addAll(generateTrafficDataDay3());

        List<TrafficDataForTimePeriod> result = testInstance.getTimePeriodsWithLeastTraffic(input, 3);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getStart()).isEqualTo(START_TIME.plusMinutes(30));
        assertThat(result.get(0).getEnd()).isEqualTo(START_TIME.plusMinutes(120));
        assertThat(result.get(0).getTrafficCount()).isEqualTo(24);

        assertThat(result.get(1).getStart()).isEqualTo(START_TIME.plusDays(2).plusMinutes(30));
        assertThat(result.get(1).getEnd()).isEqualTo(START_TIME.plusDays(2).plusMinutes(120));
        assertThat(result.get(1).getTrafficCount()).isEqualTo(24);
    }

    @Test
    void getTimePeriodsWithLeastTraffic_shouldIgnoreIncompleteDataPoints() {
        List<TrafficDataForTimePeriod> result = testInstance.getTimePeriodsWithLeastTraffic(generateIncompleteDataPoints(), 3);

        assertThat(result).isNotNull().hasSize(0);
    }

    @Test
    void getTimePeriodsWithLeastTraffic_shouldWork_forLeastTrafficAtBeginning() {
        List<TrafficDataForTimePeriod> result = testInstance.getTimePeriodsWithLeastTraffic(generateLeastTrafficAtBeginning(), 3);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getStart()).isEqualTo("2000-12-12T12:00:00");
        assertThat(result.get(0).getEnd()).isEqualTo("2000-12-12T13:30:00");
        assertThat(result.get(0).getTrafficCount()).isEqualTo(1);
    }

    @Test
    void getTimePeriodsWithLeastTraffic_shouldWork_forLeastTrafficAtTheEnd() {
        List<TrafficDataForTimePeriod> result = testInstance.getTimePeriodsWithLeastTraffic(generateLeastTrafficAtEnd(), 3);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getStart()).isEqualTo("2000-12-14T13:00:00");
        assertThat(result.get(0).getEnd()).isEqualTo("2000-12-14T14:30:00");
        assertThat(result.get(0).getTrafficCount()).isEqualTo(0);
    }

    private List<TrafficData> generateTrafficData() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(30)).trafficCount(23).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(90)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(120)).trafficCount(99).build()
        );
    }

    private List<TrafficData> generateTrafficDataDay2() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME.plusDays(1)).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(1).plusMinutes(30)).trafficCount(23).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(1).plusMinutes(60)).trafficCount(11).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(1).plusMinutes(90)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(1).plusMinutes(120)).trafficCount(100).build()
        );
    }

    private List<TrafficData> generateTrafficDataDay3() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME.plusDays(2)).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(30)).trafficCount(23).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(90)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(120)).trafficCount(99).build()
        );
    }

    private List<TrafficData> generateIncompleteDataPoints() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME.plusDays(2)).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(120)).trafficCount(99).build()
        );
    }

    private List<TrafficData> generateLeastTrafficAtBeginning() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(30)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(90)).trafficCount(10).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(120)).trafficCount(99).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2)).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(30)).trafficCount(23).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(90)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(120)).trafficCount(99).build()
        );
    }

    private List<TrafficData> generateLeastTrafficAtEnd() {
        return List.of(
                TrafficData.builder().dateTime(START_TIME).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(30)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(60)).trafficCount(1).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(90)).trafficCount(10).build(),
                TrafficData.builder().dateTime(START_TIME.plusMinutes(120)).trafficCount(99).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2)).trafficCount(5).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(30)).trafficCount(23).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(60)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(90)).trafficCount(0).build(),
                TrafficData.builder().dateTime(START_TIME.plusDays(2).plusMinutes(120)).trafficCount(0).build()
        );
    }
}