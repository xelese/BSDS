package Controller;

import Configuration.RabbitMQConfiguration;
import Model.Consumer;
import Model.Data;


import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ConsumerController {

    public ConsumerController() {
    }

    public void run() throws IOException, TimeoutException {
        Data data = new Data();
        Integer threadCount = 1;
//        ExecutorService consumerThreads = Executors.newFixedThreadPool(threadCount);
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration();

//         start threads and block to receive messages
        Thread[] recv = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Consumer(data, rabbitMQConfiguration)).start();
        }
    }
}
