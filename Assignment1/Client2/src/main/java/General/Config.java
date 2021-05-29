package General;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public final class Config {

    private final File myFile;
    private final Integer consumerThreads;
    private final Integer producerThreads;
    private final Integer queueSize;
    private final String baseUrlPath;
    private final File logFile;


    public Config(String filePath, Integer consumerThreads, Integer producerThreads) throws FileNotFoundException {
        File logFile1;

        // validate file path.
        if (filePath.isEmpty())
            throw new IllegalArgumentException("File path cannot be empty or null");
        File myFile = new File(filePath);
        if (!myFile.exists()) throw new FileNotFoundException("file " + filePath + " not found!");
        this.myFile = myFile;

        // validate consumer thread count
        if (consumerThreads == null) throw new IllegalArgumentException("number of threads cannot be null");
        if (consumerThreads <= 0) throw new IllegalArgumentException("Thread count must be greater than 0");
        this.consumerThreads = consumerThreads;

        // validate producer thread count
        if (producerThreads == null) throw new IllegalArgumentException("number of threads cannot be null");
        if (producerThreads <= 0) throw new IllegalArgumentException("Thread count must be greater than 0");
        this.producerThreads = producerThreads;

        // initialize queue size
        this.queueSize = 1000;

        // initialize log file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        logFile1 = new File("./log");
        if (!logFile1.exists()) logFile1.mkdirs();
        logFile1 = new File("./log/" + dateFormat.format(date) + "-log.csv");
        this.logFile = logFile1;
        try {
            this.logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initialize URL
        this.baseUrlPath = "http://localhost:8080/Server_war/";

        // remote url
//        this.baseUrlPath = "http://54.91.254.218:8080/Server_war/";
    }

    public File getMyFile() {
        return myFile;
    }

    public Integer getConsumerThreads() {
        return consumerThreads;
    }

    public Integer getProducerThreads() {
        return producerThreads;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public String getBaseUrlPath() {
        return baseUrlPath;
    }

    public File getLogFile() {
        return logFile;
    }
}
