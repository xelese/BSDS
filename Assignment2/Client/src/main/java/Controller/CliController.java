package Controller;

import General.Config;
import General.CustomLogger;
import Model.DataBuffer;
import Model.Consumer;
import Model.Producer;
import io.swagger.client.ApiClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CliController {

    public CliController() {
    }

    public void run(Config config) throws IllegalArgumentException, FileNotFoundException {
        // initialize the buffer containing data blocks.
        DataBuffer dataBuffer = new DataBuffer(config.getQueueSize(), config.getProducerThreads());

        // base path
        ApiClient apiClient = new ApiClient().setBasePath(config.getBaseUrlPath());

        // initialize Utils class.
        CustomLogger customLogger = new CustomLogger(dataBuffer, config);

        // function name
        String function = "wordcount"; // String | the operation to perform on the text

        //initialize timer
        long startTime;
        long endTime;

        // initialize the producer and consumer executorService thread pools
        ExecutorService producerThread = Executors.newFixedThreadPool(config.getProducerThreads());
        ExecutorService consumerThreads = Executors.newFixedThreadPool(config.getConsumerThreads());

        // ****************** Execution START ******************

        // Start timer
        startTime = System.nanoTime();

        // run producer executor pool.
        for (int i = 0; i < config.getProducerThreads(); i++) {
            producerThread.submit(new Producer(config.getInputFile(), dataBuffer, config));
        }

        // run consumer executor pool.
        for (int i = 0; i < config.getConsumerThreads(); i++) {
            consumerThreads.submit(new Consumer(dataBuffer, apiClient, function));
        }

//         terminate all threads
        producerThread.shutdown();
        consumerThreads.shutdown();

        try {
            // Force shutdown producer threads.
            if (!producerThread.awaitTermination(10, TimeUnit.MINUTES)) {
                producerThread.shutdownNow();
            }
            // Force shutdown consumer threads.
            if (!consumerThreads.awaitTermination(10, TimeUnit.MINUTES)) {
                consumerThreads.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Force close all threads on exception
            producerThread.shutdownNow();
            consumerThreads.shutdownNow();
        }

        // indication for all thread termination
        System.out.println("All threads terminated.");

        // end timer
        endTime = System.nanoTime();

        //write log data
        try {
            customLogger.writeLogData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ****************** Execution END ******************
        float wallTime = ((float) (endTime - startTime) / 1000000000.f);

        customLogger.CalculateResponse(wallTime);
    }
}