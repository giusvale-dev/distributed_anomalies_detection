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
import java.util.Base64;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @SendTo("anomalies_exchange/${binding.rabbitmq.anomaly.key}")
    public String receiveMessage(String message) throws Exception {

        ACK<Object> replyMessage = new ACK<Object>();
        String response = "";
        try {
            ObjectMapper om = new ObjectMapper();
            GenericMessagePayload mp = (GenericMessagePayload) om.readValue(message, GenericMessagePayload.class);

            if(mp != null) {
                switch (mp.getOperationType()) {
                    case INSERT:
                        replyMessage = insertAnomaly(mp.getData());
                        break;
                    case SEARCH:
                        replyMessage = loadUnresolvedAnomalies();
                        break;
                    case UPDATE:
                        replyMessage = resetToGreen(mp.getData());
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            replyMessage.setMessage(e.getMessage());
            replyMessage.setPayload(0L);
            replyMessage.setSuccess(false);
        } finally {
            ObjectMapper om = new ObjectMapper();
            response = om.writeValueAsString(replyMessage);
        }

        // Send back the ACK
        return response;
    }

    private ACK<Object> resetToGreen(AnomalyModel message) throws Exception {

        ACK<Object> replyMessage = new ACK<Object>();

        try {
            if(message != null) {
    
                Anomaly anomaly = anomalyRepository.findById(message.getId()).orElse(null);
                if(anomaly != null) {

                    anomaly.setDone(true); //Toggle to true
                    anomalyRepository.save(anomaly);
                    replyMessage.setMessage("Ok");
                    replyMessage.setPayload(anomaly.getId());
                    replyMessage.setSuccess(true);

                } else {
                    replyMessage.setMessage("Anomaly is not present");
                    replyMessage.setPayload(message.getId());
                    replyMessage.setSuccess(false);
                }

            } else {
                
                replyMessage.setMessage("Input not valid");
                replyMessage.setSuccess(false);
            }
        } catch(Exception e) {
            throw new Exception(e);
        }
        return replyMessage;
        
    }

    /**
     * Insert new anomaly in the system. If the hashCode already exist means that the anomaly already exist in the database
     * @param message
     * @throws Exception 
     */
    private ACK<Object> insertAnomaly(AnomalyModel message) throws Exception {

        ACK<Object> replyMessage = new ACK<Object>();
        try {
            if(message != null) {
    
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                
                //Hashing the anomaly details
                String hashCode = new String(md.digest(message.getDetails().getBytes()));
                String hashCodeBase64 = new String(Base64.getEncoder().encode(hashCode.getBytes()));
                
                if(anomalyRepository.findByHashCode(hashCode) == null) {
    
                    Anomaly anomaly = new Anomaly();
                    anomaly.setDate(message.getDatetime());
                    anomaly.setDescription(message.getDetails());
                    anomaly.setDone(false);
                    anomaly.setHashCode(hashCodeBase64);
                    anomaly.setHostname(message.getHostname());
                    anomaly.setIpAddress(message.getIpAddress());
                    Anomaly persistent = anomalyRepository.save(anomaly);
                    replyMessage.setMessage("Ok");
                    replyMessage.setPayload(persistent.getId());
                    replyMessage.setSuccess(true);
    
                } else {
                    replyMessage.setMessage(hashCode + " is already present");
                    replyMessage.setPayload(0L);
                    replyMessage.setSuccess(false);
                }
            } else {
                
                replyMessage.setMessage("Input not valid");
                replyMessage.setSuccess(false);
            }
        } catch(Exception e) {
            throw new Exception(e);
        }
        return replyMessage;
    }

    private ACK<Object> loadUnresolvedAnomalies() throws Exception {

        ACK<Object> replyMessage = new ACK<Object>();
        try {
            List<Anomaly> lst = anomalyRepository.loadAllUnresolvedAnomalies();
            replyMessage.setMessage("Ok");
            replyMessage.setSuccess(true);
            replyMessage.setPayload(lst);

        } catch(Exception e) {
            throw new Exception(e);
        }
        return replyMessage;

    }
    
}
