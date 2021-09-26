package com.seek.trafficdataapp.file;

import com.seek.trafficdataapp.model.TrafficData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * this is used to generate large test files
 */
@Disabled
public class TestFileGenerator {
    private static final LocalDateTime START_TIME = LocalDateTime.of(2000, 12, 12, 12, 0, 0, 0);

    @Test
    void generateLargeTestFile() throws IOException {
        FileManager fileManager = new FileManager();

        List<TrafficData> testData = new ArrayList<>();
        LocalDateTime start = START_TIME;
        for (int i = 0; i < 100000; i++) {
            Random rand = new Random();
            testData.add(TrafficData.builder().dateTime(start).trafficCount(rand.nextInt(100)).build());
            start = start.plusMinutes(30);
        }
        fileManager.stringToFile(
                testData.stream().map(TrafficData::toString).collect(Collectors.joining(System.lineSeparator())),
                "99_mass_input.txt");
    }
}
