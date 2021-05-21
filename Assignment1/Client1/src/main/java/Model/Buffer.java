package Model;

import java.util.concurrent.*;

public class Buffer {

    protected BlockingQueue<String> queue;

    public Buffer(Integer queueSize) throws IllegalArgumentException {
        // validate que size parameter
        if (queueSize == null) throw new IllegalArgumentException("Queue size cannot be null");
        if (queueSize <= 0) throw new IllegalArgumentException("Queue size must be greater than 0");
        this.queue = new ArrayBlockingQueue<>(queueSize);
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }
}
