import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChannelConsumer implements Runnable {
    private final Connection connection;
    private final static String QUEUE_NAME = "TestRabbitMQ";
    private final DynamoDBMapper mapper;

    public ChannelConsumer(Connection connection, DynamoDBMapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
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
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            try {
                List<Data> list = processMessage(message);
                if (list != null)
                    mapper.batchSave(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // process messages
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    private List<Data> processMessage(String message) {
        if (message.isEmpty()) return null;
        String[] pairs = message.substring(1, message.length() - 1).split(",");
        List<Data> list = new ArrayList<>();
        for (String pair : pairs) {
            int index = pair.lastIndexOf("=");
            String key = pair.substring(0, index).trim();
            String val = pair.substring(index + 1);
            Data data = new Data();
            data.setKey(key);
            data.setVal(val);
            list.add(data);
        }
        return list;
    }
}
