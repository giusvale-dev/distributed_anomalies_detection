package it.uniroma1.databaseservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.repositories.MemberRepository;

@Service
public class MessageListener {

    @Autowired
    private MemberRepository memberRepository;
    
    @RabbitListener(queues = {"${queue.rabbitmq.listener.name}"})
    @SendTo("user_exchange/${binding.rabbitmq.key}")
    public String receiveMessage(String message) throws JsonProcessingException {

        ACK<Member> replyMessage = new ACK<Member>();
        
        if(message != null) {
            ObjectMapper om = new ObjectMapper();
            MessagePayload mp = (MessagePayload) om.readValue(message, MessagePayload.class);
            if(mp != null) {        
                switch(mp.getOperationType()) {
                    case INSERT:
                        Member m = mp.getUser();
                        if(memberRepository.findByUsername(m.getUsername()) == null) {
                            m = memberRepository.save(m);
                            replyMessage.setMessage("Ok");
                            replyMessage.setPayload(m);
                            replyMessage.setSuccess(true);
                        } else {
                            replyMessage.setMessage("User exist");
                            replyMessage.setPayload(new Member());
                            replyMessage.setSuccess(false);
                        }
                        
                    break;
                    default:
                        replyMessage.setMessage("Operation not supported");
                        replyMessage.setPayload(new Member());
                        replyMessage.setSuccess(false);
                    break;
                }
            } else {
                replyMessage.setMessage("Unparsable message");
                replyMessage.setPayload(new Member());
                replyMessage.setSuccess(false);
            }
            
        } else {
            replyMessage.setMessage("Unparsable message");
            replyMessage.setPayload(new Member());
            replyMessage.setSuccess(false);
        }
        
        ObjectMapper om = new ObjectMapper();
        String response = om.writeValueAsString(replyMessage);

        //Send back the ACK
        return response;
    }

}
