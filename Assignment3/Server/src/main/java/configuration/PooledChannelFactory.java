package configuration;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


public class PooledChannelFactory extends BasePooledObjectFactory<Channel> {

    private final RabbitmqConfiguration configuration;

    public PooledChannelFactory() {
        super();
        configuration = new RabbitmqConfiguration();
    }

    @Override
    public Channel create() throws Exception {
        Channel channel = configuration.getConn().createChannel();
        channel.exchangeDeclare("TestRabbitMQExchange", "fanout");
        channel.queueDeclare("TestRabbitMQ", true, false, false, null);
        channel.queueBind("TestRabbitMQ", "TestRabbitMQExchange", "");
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }
}
