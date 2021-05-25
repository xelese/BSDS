package Controller;

import General.Config;
import Model.DataBuffer;
import Model.Consumer;
import Model.Producer;
import io.swagger.client.ApiClient;

import java.io.FileNotFoundException;
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

        // function name
        String function = "function_example"; // String | the operation to perform on the text

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
            producerThread.submit(new Producer(config.getMyFile(), dataBuffer));
        }

        // run consumer executor pool.
        for (int i = 0; i < config.getConsumerThreads(); i++) {
            consumerThreads.submit(new Consumer(dataBuffer, apiClient, function));
        }

        // terminate all threads
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

        // end timer
        endTime = System.nanoTime();

        // indication for all thread termination
        System.out.println("All threads terminated.");

        // ****************** Execution END ******************
        float wallTime = ((float) (endTime - startTime) / 1000000000.f);
        float throughput = dataBuffer.getSuccessCounter().floatValue() / wallTime;

        System.out.println("------------------ CONFIG --------------------------");
        System.out.println("Total number of producer Threads: " + config.getProducerThreads());
        System.out.println("Total number of consumer Threads: " + config.getConsumerThreads());
        System.out.println("Total number of client requests consumed: " + dataBuffer.getTextLineCounter());
        System.out.println("------------------ OVERALL STATS -------------------");
        System.out.println("1. total number of successful requests sent: " + dataBuffer.getSuccessCounter());
        System.out.println("2. total number of unsuccessful requests sent: " + dataBuffer.getFailCounter());
        System.out.println("3. wall time: " + wallTime + " s");
        System.out.println("4. Throughput: " + throughput + " req/s");
        System.out.println("---------------------------------------------------");
    }
}