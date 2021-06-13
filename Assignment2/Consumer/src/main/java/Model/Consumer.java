package Model;

import Configuration.RabbitMQConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.GetResponse;

public class Consumer implements Runnable {

    private final Data data;
    private final RabbitMQConfiguration configuration;
    private Channel channel;


    public Consumer(Data data, RabbitMQConfiguration configuration) throws IOException {
        this.data = data;
        this.configuration = configuration;
        createChannel();
    }


    @Override
    public void run() {
        try {
            System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
//         max one message per receiver
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                processMessage(message);
                System.out.println("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
            };

            // process messages
//        while (true) {
//            try {
//                GetResponse ch = channel.basicGet(configuration.getQueueName(), false);
//                if (ch != null) {
//                    ch.getEnvelope().getDeliveryTag();
//                    String message = new String(ch.getBody());
//                    channel.basicAck(ch.getEnvelope().getDeliveryTag(), false);
//                    processMessage(message);
//                    System.out.println("thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
//                }
            // process messages
            channel.basicConsume(configuration.getQueueName(), false, deliverCallback, consumerTag -> {
            });
        } catch (IOException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createChannel() throws IOException {
        final Channel channel = this.configuration.getConnection().createChannel();
        channel.queueDeclare(configuration.getQueueName(), true, false, false, null);
        channel.basicQos(1);
        this.channel = channel;
    }

    private void processMessage(String theStringToParse) {
        String pairs = (theStringToParse.substring(1, theStringToParse.length() - 1));
//        for (String pair : pairs) {
            String[] keyValue = pairs.split(":");
            this.data.getConcurrentHashmap().put(keyValue[0], this.data.getConcurrentHashmap().getOrDefault(keyValue[0], 0) + Integer.parseInt(keyValue[1]));
//        }
    }

}
