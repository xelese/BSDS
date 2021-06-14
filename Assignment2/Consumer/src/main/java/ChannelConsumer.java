import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class ChannelConsumer implements Runnable {
    private final Connection connection;
    private final static String QUEUE_NAME = "TestRabbitMQ";
    private final Data data;

    public ChannelConsumer(Connection connection, String name, Data data) {
        this.connection = connection;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doWork() throws Exception {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("TestRabbitMQExchange", "fanout");
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, "TestRabbitMQExchange", "");
        channel.basicQos(1);
        System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            processMessage(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        // process messages
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
    }

    private void processMessage(String message) {
        if (message.isEmpty()) return;
        String[] pairs = message.substring(1, message.length() - 1).split(",");
        for (String pair : pairs) {
            int index = pair.lastIndexOf(":");
            String key = pair.substring(0, index);
            Integer val = Integer.parseInt(pair.substring(index + 1));
            this.data.getMap().put(key, this.data.getMap().getOrDefault(key, 0) + val);
        }
    }
}
