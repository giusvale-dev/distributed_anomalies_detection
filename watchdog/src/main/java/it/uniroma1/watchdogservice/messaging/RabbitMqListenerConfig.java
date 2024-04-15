package it.uniroma1.watchdogservice.messaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMqListenerConfig {

    @Value("${queue.rabbitmq.listener.name}")
    private String queueName;

    @Value("${binding.rabbitmq.key}")
    private String keyBinding;

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("watchdog_exchange");
    }

    @Bean
    Binding bindingA(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(keyBinding);
    }
}
