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

package it.uniroma1.message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessageProducer {

    private Connection connection = null;
    private Channel channel = null;
    private static final String EXCHANGE_NAME = "anomalies_exchange";
    private static final String QUEUE_NAME = "anomalies_queue";
    private static final String ROUTING_KEY = "anomalies_key";


    public MessageProducer() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
    }

    public void close() throws IOException, TimeoutException{
        this.channel.close();
        this.connection.close();
    }

    public void sendMessage(String message) throws IOException {

        if(this.channel != null && message != null) {
            this.channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
        }
    }

}
