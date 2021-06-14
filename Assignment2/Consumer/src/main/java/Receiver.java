import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Receiver {
    private final static Integer threads = 256;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("3.225.35.105");
        final Connection connection = factory.newConnection();
        final Data data = new Data();
        for (int i = 0; i < threads; i++) {
            new Thread(new ChannelConsumer(connection, "Thread: " + i, data)).start();
        }
    }
}
