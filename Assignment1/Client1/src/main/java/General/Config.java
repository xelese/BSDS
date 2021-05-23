package General;

import java.io.File;
import java.io.FileNotFoundException;

public final class Config {

    private final File myFile;
    private final Integer consumerThreads;
    private final Integer producerThreads;
    private final Integer queueSize;
    private final String baseUrlPath;


    public Config(String filePath, Integer consumerThreads, Integer producerThreads) throws FileNotFoundException {

        // validate file path.
        if (filePath.isEmpty() || filePath == null)
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

        // initialize URL
        this.baseUrlPath = "http://localhost:8080/gortonator/TextProcessor/1.0.2/";
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
}
