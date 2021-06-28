package General;

import Model.DataBuffer;
import Model.LoggingData;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;


public class CustomLogger {

    private final DataBuffer dataBuffer;
    private final Config config;

    public CustomLogger(DataBuffer dataBuffer, Config config) {
        this.dataBuffer = dataBuffer;
        this.config = config;
    }

    public void writeLogData() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(this.config.getLogFile()));

        Collections.sort(this.dataBuffer.getLoggingDataList());

        String[] line = new String[4];
        line[0] = "Start Time";
        line[1] = "Request Type";
        line[2] = "Latency (ms)";
        line[3] = "Response Code";
        csvWriter.writeNext(line);

        for (LoggingData loggingData : dataBuffer.getLoggingDataList()) {
            line[0] = loggingData.getStartTime();
            line[1] = loggingData.getRequestType();
            line[2] = loggingData.getLatency().toString();
            line[3] = loggingData.getResponseCode().toString();
            csvWriter.writeNext(line);
        }
        csvWriter.close();
    }

    public void CalculateResponse(float wallTime) {
        float throughput = dataBuffer.getSuccessCounter().floatValue() / wallTime;
        float mean;
        float median;
        float max = Float.MIN_VALUE;
        float sum = 0;
        int size = dataBuffer.getLoggingDataList().size();
        for (LoggingData loggingData : dataBuffer.getLoggingDataList()) {
            sum += loggingData.getLatency();
        }
        mean = sum / size;
        if (size % 2 == 1) {
            median = dataBuffer.getLoggingDataList().get((size / 2) - 1).getLatency();
        } else {
            median = (dataBuffer.getLoggingDataList().get((size / 2) - 2).getLatency() +
                    dataBuffer.getLoggingDataList().get((size / 2) - 1).getLatency()) / 2.f;
        }
        max = dataBuffer.getLoggingDataList().get(size - 1).getLatency();

        float ninetiethPercentile = dataBuffer.getLoggingDataList().get((int) Math.ceil(0.9 * size)).getLatency();

        System.out.println("------------------ CONFIG --------------------------");
        System.out.println("Total number of producer Threads: " + config.getProducerThreads());
        System.out.println("Total number of consumer Threads: " + config.getConsumerThreads());
        System.out.println("Total number of client requests consumed: " + dataBuffer.getTextLineCounter());
        System.out.println("Log File location: " + config.getLogFile().getPath());
        System.out.println("------------------ OVERALL STATS -------------------");
        System.out.println("1. Total number of successful requests sent: " + dataBuffer.getSuccessCounter());
        System.out.println("2. Total number of unsuccessful requests sent: " + dataBuffer.getFailCounter());
        System.out.println("3. Total wall time: " + wallTime + " s");
        System.out.println("4. Throughput: " + throughput + " req/s");
        System.out.println("------------------ DETAILED STATS ------------------");
        System.out.println("1. Mean Response Time: " + mean + " ms");
        System.out.println("1. Median Response Time: " + median + " ms");
        System.out.println("1. Max Response Time: " + max + " ms");
        System.out.println("1. 90th Percentile: " + ninetiethPercentile + " ms");
        System.out.println("----------------------------------------------------");
    }
}
