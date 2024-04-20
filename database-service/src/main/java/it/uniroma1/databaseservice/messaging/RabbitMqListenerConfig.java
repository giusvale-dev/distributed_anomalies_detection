/**
 * MIT No Attribution
 *
 *Copyright 2024 Giuseppe Valente <valentepeppe@gmail.com>
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *software and associated documentation files (the "Software"), to deal in the Software
 *without restriction, including without limitation the rights to use, copy, modify,
 *merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *permit persons to whom the Software is furnished to do so.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.uniroma1.databaseservice.messaging;

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

    @Value("${queue.rabbitmq.listener.anomaly}")
    private String queueAnomalyName;

    @Value("${binding.rabbitmq.key}")
    private String keyBinding;

    @Value("${binding.rabbitmq.anomaly.key}")
    private String keyBindingAnomaly;

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    public Queue anomalyQueue() {
        return new Queue(queueAnomalyName, false);
    }

    @Bean(name = "userExchange")
    public DirectExchange exchange() {
        return new DirectExchange("user_exchange");
    }

    @Bean(name = "anomalyExchange")
    public DirectExchange anomalyExchange() {
        return new DirectExchange("anomalies_exchange");
    }

    @Bean
    Binding bindingA() {
        return BindingBuilder.bind(queue()).to(exchange()).with(keyBindingAnomaly);
    }

    @Bean
    Binding bindingB() {
        return BindingBuilder.bind(anomalyQueue()).to(anomalyExchange()).with(keyBinding);
    }

    
}
