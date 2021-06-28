package configuration;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqConfiguration {

    private Connection conn;

    public RabbitmqConfiguration() {
        createConnection();
    }

    public Connection getConn() {
        if (conn == null) createConnection();
        return conn;
    }

    private void createConnection() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("3.225.35.105");
        connectionFactory.setRequestedChannelMax(1000);
        try {
            conn = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
