package com.seek.trafficdataapp.cli;

import com.seek.trafficdataapp.file.FileManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TrafficDataCommandLineRunnerIntegrationTest {
    private static final String INPUT =
            "2016-12-01T05:00:00 5" + System.lineSeparator() +
                    "2016-12-01T05:30:00 12" + System.lineSeparator() +
                    "2016-12-01T06:00:00 14" + System.lineSeparator() +
                    "2016-12-01T06:30:00 15" + System.lineSeparator() +
                    "2016-12-01T07:00:00 25" + System.lineSeparator() +
                    "2016-12-01T07:30:00 46" + System.lineSeparator() +
                    "2016-12-01T08:00:00 42" + System.lineSeparator() +
                    "2016-12-01T15:00:00 9" + System.lineSeparator() +
                    "2016-12-01T15:30:00 11" + System.lineSeparator() +
                    "2016-12-01T23:30:00 0" + System.lineSeparator() +
                    "2016-12-05T09:30:00 18" + System.lineSeparator() +
                    "2016-12-05T10:30:00 15" + System.lineSeparator() +
                    "2016-12-05T11:30:00 7" + System.lineSeparator() +
                    "2016-12-05T12:30:00 6" + System.lineSeparator() +
                    "2016-12-05T13:30:00 9" + System.lineSeparator() +
                    "2016-12-05T14:30:00 11" + System.lineSeparator() +
                    "2016-12-05T15:30:00 15" + System.lineSeparator() +
                    "2016-12-08T18:00:00 33" + System.lineSeparator() +
                    "2016-12-08T19:00:00 28" + System.lineSeparator() +
                    "2016-12-08T20:00:00 25" + System.lineSeparator() +
                    "2016-12-08T21:00:00 21" + System.lineSeparator() +
                    "2016-12-08T22:00:00 16" + System.lineSeparator() +
                    "2016-12-08T23:00:00 11" + System.lineSeparator() +
                    "2016-12-09T00:00:00 4";

    private static final String TOTAL_CARS = "398";

    private static final String TOP3 =
            "2016-12-01T07:30:00 46" + System.lineSeparator() +
                    "2016-12-01T08:00:00 42" + System.lineSeparator() +
                    "2016-12-08T18:00:00 33";

    private static final String DAILY =
            "2016-12-01 179" + System.lineSeparator() +
                    "2016-12-05 81" + System.lineSeparator() +
                    "2016-12-08 134" + System.lineSeparator() +
                    "2016-12-09 4";

    private static final String LEAST_TRAFFIC = "2016-12-01T05:00:00 2016-12-01T06:30:00 31";
    @Autowired
    private TrafficDataCommandLineRunner testInstance;

    @MockBean
    private FileManager fileManager;

    @Test
    void run_shouldProcessFileAndGenerateOutput() throws IOException {
        when(fileManager.fileToString(anyString())).thenReturn(INPUT);
        doNothing().when(fileManager).stringToFile(anyString(), anyString());

        ArgumentCaptor<String> outputFiles = ArgumentCaptor.forClass(String.class);
        testInstance.run("file1.txt");

        verify(fileManager, times(4)).stringToFile(outputFiles.capture(), anyString());

        List<String> outputContent = outputFiles.getAllValues();

        assertThat(outputContent).isNotNull().hasSize(4);

        assertThat(outputContent.get(0)).isEqualTo(TOTAL_CARS);
        assertThat(outputContent.get(1)).isEqualTo(TOP3);
        assertThat(outputContent.get(2)).isEqualTo(DAILY);
        assertThat(outputContent.get(3)).isEqualTo(LEAST_TRAFFIC);
    }
}