package it.uniroma1.databaseservice.messaging;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Authority;
import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.entities.models.UserUI;
import it.uniroma1.databaseservice.repositories.AuthorityRepository;
import it.uniroma1.databaseservice.repositories.MemberRepository;
import jakarta.transaction.Transactional;

@Service
public class AnomaliesListener {

    

    @RabbitListener(queues = { "${queue.rabbitmq.listener.name}" })
    @SendTo("user_exchange/${binding.rabbitmq.key}")
    public String receiveMessage(String message) throws JsonProcessingException {

        ACK<Object> replyMessage = new ACK<Object>();
        String response = "";
        try {
            if (message != null) {
                ObjectMapper om = new ObjectMapper();
                MessagePayload mp = (MessagePayload) om.readValue(message, MessagePayload.class);
                Member m = null;
                if (mp != null) {
                    switch (mp.getOperationType()) {
                        
                        default:
                            replyMessage.setMessage("Operation not supported");
                            replyMessage.setPayload(0L);
                            replyMessage.setSuccess(false);
                            break;
                    }
                } else {
                    replyMessage.setMessage("Unparsable message");
                    replyMessage.setPayload(0L);
                    replyMessage.setSuccess(false);
                }
            } else {
                replyMessage.setMessage("Unparsable message");
                replyMessage.setPayload(0L);
                replyMessage.setSuccess(false);
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

}
