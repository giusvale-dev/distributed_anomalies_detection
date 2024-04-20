package it.uniroma1.databaseservice.messaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import it.uniroma1.databaseservice.entities.Anomaly;
import it.uniroma1.databaseservice.repositories.AnomalyRepository;

@Service
public class AnomalyListener {

    @Autowired
    private AnomalyRepository anomalyRepository;
    
    
    @RabbitListener(queues = { "${queue.rabbitmq.listener.anomaly}" })
    public void receiveMessage(String message) throws Exception {

        try {
            insertAnomaly(message);
            
        } catch(Exception e) {
            if(e instanceof DuplicateKeyException) {
                return;
            } else {
                throw new Exception(e);
            }
        }
    }

    /**
     * Insert new anomaly in the system. If the hashCode already exist means that the anomaly already exist in the database
     * @param message
     * @throws NoSuchAlgorithmException
     */
    private void insertAnomaly(String message) throws NoSuchAlgorithmException {
        if(message != null) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hashCode = new String(md.digest(message.getBytes()));
            
            if(anomalyRepository.findByHashCode(hashCode) == null) {

                Anomaly anomaly = new Anomaly();
                anomaly.setDate(new Date());
                anomaly.setDescription(message);
                anomaly.setDone(false);
                anomaly.setHashCode(hashCode);
                anomalyRepository.save(anomaly);

            } else {
                throw new DuplicateKeyException(hashCode + " is already present");
            }
        }
    }     
}
