package Model;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBuffer {

    protected BlockingQueue<String> queue;
    protected AtomicInteger textLineCounter;
    protected AtomicInteger successCounter;

    public DataBuffer(Integer queueSize) throws IllegalArgumentException {
        // validate que size parameter
        if (queueSize == null) throw new IllegalArgumentException("Queue size cannot be null");
        if (queueSize <= 0) throw new IllegalArgumentException("Queue size must be greater than 0");
        this.queue = new ArrayBlockingQueue<>(queueSize);

        // set textLineCounter to 0
        this.textLineCounter = new AtomicInteger(0);

        // set successCounter to 0
        this.successCounter = new AtomicInteger(0);
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public AtomicInteger getTextLineCounter() {
        return textLineCounter;
    }

    public AtomicInteger getSuccessCounter() {
        return successCounter;
    }
}
