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
                        insertAnomaly(mp.getData());
                        break;
                    case SEARCH:
                        break;
                    default:
                        break;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
            return; //To avoid the insertion of the same message in the queue
        }
    }

    /**
     * Insert new anomaly in the system. If the hashCode already exist means that the anomaly already exist in the database
     * @param message
     * @throws NoSuchAlgorithmException
     */
    private void insertAnomaly(AnomalyModel message) throws NoSuchAlgorithmException {
        if(message != null) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            //Hashing the anomaly details
            String hashCode = new String(md.digest(message.getDetails().getBytes()));
            
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
