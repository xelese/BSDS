package Configuration;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class RabbitMQConfiguration {

    private final String queueName;
    private final String host;
    private Connection connection;

    public RabbitMQConfiguration() throws IOException, TimeoutException {
        this.queueName = "TestRabbitMQ";
        this.host = "localhost";
        createConnection();
    }

    public String getQueueName() {
        return queueName;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createConnection() throws IOException, TimeoutException {
        if (this.connection == null) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(this.host);
            factory.setRequestedChannelMax(300);
            this.connection = factory.newConnection();
        }
    }
}
