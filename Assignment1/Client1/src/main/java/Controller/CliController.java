package Controller;

import Model.DataBuffer;
import Model.Consumer;
import Model.Producer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CliController {

    public CliController() {
    }

    public void run(File file, Integer threadCount, Integer queueSize) throws IllegalArgumentException {
        // initialize the buffer containing blockingQueue.
        DataBuffer dataBuffer = new DataBuffer(queueSize);

        // thread name object.
        String internalThreadName;

        // initialize the producer and consumer executorService thread pools
        ExecutorService producerThread = Executors.newSingleThreadExecutor();
        ExecutorService consumerThreads = Executors.newFixedThreadPool(threadCount);

        // run the producer executor.
        Producer producer = new Producer(file, dataBuffer, threadCount);
        producerThread.submit(producer);

        // run consumer executor pool.
        for (int i = 0; i < threadCount; i++) {
            // name each thread.
            internalThreadName = "Thread " + i;
            consumerThreads.submit(new Consumer(internalThreadName, dataBuffer));
        }

        // terminate all threads
        producerThread.shutdown();
        consumerThreads.shutdown();

        try {
            // Force shutdown producer threads.
            if (!producerThread.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                producerThread.shutdownNow();
            }
            // Force shutdown consumer threads.
            if (!consumerThreads.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                consumerThreads.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Force close all threads on exception
            producerThread.shutdownNow();
            consumerThreads.shutdownNow();
        }

        if (producer.getDebugLineCounter() == dataBuffer.getTextLineCounter().get()) System.out.println("true");
        else System.out.println("false");

        // indication for all thread termination
        System.out.println("All threads terminated.");
    }
}
