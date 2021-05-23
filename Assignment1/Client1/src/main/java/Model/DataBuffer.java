package Model;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBuffer {

    protected BlockingQueue<String> queue;
    protected AtomicInteger textLineCounter;
    protected AtomicInteger successCounter;
    protected AtomicInteger failCounter;
    protected AtomicInteger producerComplete;

    public DataBuffer(Integer queueSize, Integer producerCount) throws IllegalArgumentException {
        // validate que size parameter
        if (queueSize == null) throw new IllegalArgumentException("Queue size cannot be null");
        if (queueSize <= 0) throw new IllegalArgumentException("Queue size must be greater than 0");
        this.queue = new ArrayBlockingQueue<>(queueSize);

        // set textLineCounter to 0
        this.textLineCounter = new AtomicInteger(0);

        // set successCounter to 0
        this.successCounter = new AtomicInteger(0);

        // set successCounter to 0
        this.failCounter = new AtomicInteger(0);

        // set total producers
        this.producerComplete = new AtomicInteger(producerCount);
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public AtomicInteger getSuccessCounter() {
        return successCounter;
    }

    public AtomicInteger getFailCounter() {
        return failCounter;
    }
}
