package configuration;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class PooledChannel {

    private final GenericObjectPool<Channel> pool;

    public PooledChannel(GenericObjectPool<Channel> pool) {
        this.pool = pool;
    }

    public GenericObjectPool<Channel> getPool() {
        return pool;
    }
}
