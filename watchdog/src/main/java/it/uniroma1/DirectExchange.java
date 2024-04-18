package it.uniroma1;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectExchange {
    public static void declareExchange() throws IOException, TimeoutException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("my-direct-exchange", BuiltinExchangeType.DIRECT, true);
        channel.close();
    }

    //Publish the message
    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        String message = " When a new anomaly is triggered, send a new message to the direct exchange ";
        channel.basicPublish("my-direct-exchange", "homeAppliance", null, message.getBytes());
        channel.close();
    }


}

