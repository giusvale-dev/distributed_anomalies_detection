package it.uniroma1.databaseservice.messaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Anomaly;
import it.uniroma1.databaseservice.entities.models.AnomalyModel;
import it.uniroma1.databaseservice.repositories.AnomalyRepository;

@Service
public class AnomalyListener {

    @Autowired
    private AnomalyRepository anomalyRepository;
    
    
    
    @RabbitListener(queues = { "${queue.rabbitmq.listener.anomaly}" })
    public void receiveMessage(String message) throws Exception {

        try {
            ObjectMapper om = new ObjectMapper();
            GenericMessagePayload mp = (GenericMessagePayload) om.readValue(message, GenericMessagePayload.class);

            if(mp != null) {
                switch (mp.getOperationType()) {
                    case INSERT:
                        insertAnomaly(mp.getData(), message);
                        break;
                    case SEARCH:
                        break;
                
                    default:
                        break;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Insert new anomaly in the system. If the hashCode already exist means that the anomaly already exist in the database
     * @param message
     * @throws NoSuchAlgorithmException
     */
    private void insertAnomaly(AnomalyModel message, String json) throws NoSuchAlgorithmException {
        if(message != null && json != null) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hashCode = new String(md.digest(json.getBytes()));
            
            if(anomalyRepository.findByHashCode(hashCode) == null) {

                Anomaly anomaly = new Anomaly();
                anomaly.setDate(message.getDatetime());
                anomaly.setDescription(message.getDetails());
                anomaly.setDone(false);
                anomaly.setHashCode(hashCode);
                anomaly.setHostname(message.getHostname());
                anomaly.setIpAddress(message.getIpAddress());
                anomalyRepository.save(anomaly);

            } else {
                throw new DuplicateKeyException(hashCode + " is already present");
            }
        }
    }     
}
