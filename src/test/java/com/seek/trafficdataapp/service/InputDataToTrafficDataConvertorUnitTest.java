package com.seek.trafficdataapp.service;

import com.seek.trafficdataapp.model.TrafficData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InputDataToTrafficDataConvertorUnitTest {
    private InputDataToTrafficDataConvertor testInstance;

    @BeforeEach
    void setTestInstance() {
        testInstance = new InputDataToTrafficDataConvertor();
    }

    @Test
    void getTrafficDataFromFileContent_shouldParseValidContent_andSortByDateTime() {
        String content =
                "2016-12-01T05:00:00 5" + System.lineSeparator() +
                        "2016-12-02T06:00:00 14" + System.lineSeparator() +
                        "2016-12-01T05:30:00 12";

        List<TrafficData> result = testInstance.getTrafficDataFromFileContent(content);

        assertThat(result).isNotNull().hasSize(3);

        assertThat(result.get(0).getDateTime()).isEqualTo("2016-12-01T05:00:00");
        assertThat(result.get(0).getTrafficCount()).isEqualTo(5);

        assertThat(result.get(1).getDateTime()).isEqualTo("2016-12-01T05:30:00");
        assertThat(result.get(1).getTrafficCount()).isEqualTo(12);

        assertThat(result.get(2).getDateTime()).isEqualTo("2016-12-02T06:00:00");
        assertThat(result.get(2).getTrafficCount()).isEqualTo(14);
    }

    @Test
    void getTrafficDataFromFileContent_shouldIgnoreEmptyLines() {
        String content =
                "2016-12-01T05:00:00 5" + System.lineSeparator() +
                        "" + System.lineSeparator() +
                        "2016-12-01T05:30:00 12" + System.lineSeparator() +
                        "";

        List<TrafficData> result = testInstance.getTrafficDataFromFileContent(content);

        assertThat(result).isNotNull().hasSize(2);

        assertThat(result.get(0).getDateTime()).isEqualTo("2016-12-01T05:00:00");
        assertThat(result.get(0).getTrafficCount()).isEqualTo(5);


        assertThat(result.get(1).getDateTime()).isEqualTo("2016-12-01T05:30:00");
        assertThat(result.get(1).getTrafficCount()).isEqualTo(12);
    }
}