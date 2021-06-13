package io.swagger.configuration;


import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMQConfiguration {

    private final String rabbitQueueName = "TestRabbitMQ";

    @Bean
    org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory() {
        ConnectionFactory rabbitConnectionFactory = new ConnectionFactory();
        rabbitConnectionFactory.setHost("localhost");
        rabbitConnectionFactory.setRequestedChannelMax(1000);
        PooledChannelConnectionFactory pcf = new PooledChannelConnectionFactory(rabbitConnectionFactory);
        pcf.setPoolConfigurer((pool, tx) -> {
            if (tx) {
                // configure the transactional pool
                pool.setMaxTotal(300);
                pool.setMinIdle(128);
                pool.setMaxIdle(256);
            } else {
                // configure the non-transactional pool
                pool.setMaxTotal(300);
                pool.setMinIdle(128);
                pool.setMaxIdle(256);
            }
        });
        return pcf;
    }

    @Bean
    public RabbitAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public AmqpTemplate rabbitMQTemplate() {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue myQueue() {
        return new Queue(this.rabbitQueueName);
    }

    @Bean
    public DirectExchange exchange() {
        String rabbitExchangeName = "TestRabbitMQExchange";
        return new DirectExchange(rabbitExchangeName);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(this.rabbitQueueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
