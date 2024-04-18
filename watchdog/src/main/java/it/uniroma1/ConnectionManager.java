package it.uniroma1;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ConnectionManager {

    private ConnectionManager() {}

    private static class ConnectionHolder {
        private static final Connection INSTANCE = createConnection();

        private static Connection createConnection() {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
                return connectionFactory.newConnection();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException("Failed to create a connection", e);
            }
        }
    }

    // Public method to get the instance of Connection
    public static Connection getConnection() {
        return ConnectionHolder.INSTANCE;
    }
}