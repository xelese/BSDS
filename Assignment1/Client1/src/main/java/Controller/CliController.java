package Controller;

import Model.DataBuffer;
import Model.Consumer;
import Model.Producer;
import io.swagger.client.ApiClient;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CliController {

    public CliController() {
    }

    public void run(File file, Integer threadCount, Integer queueSize) throws IllegalArgumentException {
        // initialize the buffer containing data blocks.
        DataBuffer dataBuffer = new DataBuffer(queueSize);

        // initialize Producer
        Producer producer = new Producer(file, dataBuffer, threadCount);

        // thread name object.
        String internalThreadName;

        // base path
        String basePath = "http://localhost:8080/gortonator/TextProcessor/1.0.2/";
        ApiClient apiClient = new ApiClient().setBasePath(basePath);

        // function name
        String function = "function_example"; // String | the operation to perform on the text

        //initialize timer
        long startTime;
        long endTime;

        // initialize the producer and consumer executorService thread pools
        ExecutorService producerThread = Executors.newSingleThreadExecutor();
        ExecutorService consumerThreads = Executors.newFixedThreadPool(threadCount);

        // ****************** Execution START ******************

        // Start timer
        startTime = System.nanoTime();

        // run the producer executor.
        producerThread.submit(producer);

        // run consumer executor pool.
        for (int i = 0; i < threadCount; i++) {
            // name each thread.
            internalThreadName = "Thread " + i;
            consumerThreads.submit(new Consumer(internalThreadName, dataBuffer, apiClient, function));
        }

        // terminate all threads
        producerThread.shutdown();
        consumerThreads.shutdown();

        try {
            // Force shutdown producer threads.
            if (!producerThread.awaitTermination(1, TimeUnit.MINUTES)) {
                producerThread.shutdownNow();
            }
            // Force shutdown consumer threads.
            if (!consumerThreads.awaitTermination(1, TimeUnit.MINUTES)) {
                consumerThreads.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Force close all threads on exception
            producerThread.shutdownNow();
            consumerThreads.shutdownNow();
        }

        // end timer
        endTime = System.nanoTime();

        // ****************** Execution END ******************

        // print timer statistics
        System.out.println((endTime - startTime)/1000000 + " ms");

        if (producer.getDebugLineCounter() == dataBuffer.getTextLineCounter().get()) System.out.println("true");
        else System.out.println("false");

        System.out.println("Successful messages: " + dataBuffer.getSuccessCounter());

        // indication for all thread termination
        System.out.println("All threads terminated.");
    }
}
