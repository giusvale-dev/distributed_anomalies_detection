package it.uniroma1.databaseservice.messaging;

import java.util.HashSet;
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
import it.uniroma1.databaseservice.repositories.AuthorityRepository;
import it.uniroma1.databaseservice.repositories.MemberRepository;

@Service
public class MessageListener {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @RabbitListener(queues = {"${queue.rabbitmq.listener.name}"})
    @SendTo("user_exchange/${binding.rabbitmq.key}")
    public String receiveMessage(String message) throws JsonProcessingException {

        ACK<Member> replyMessage = new ACK<Member>();
        
        if(message != null) {
            ObjectMapper om = new ObjectMapper();
            MessagePayload mp = null;
            try {
                mp = (MessagePayload) om.readValue(message, MessagePayload.class);
            } catch(JsonProcessingException e) {
                // do nothing
            }
            if(mp != null) {        
                switch(mp.getOperationType()) {
                    case INSERT:
                        Member m = mp.getUser();
                        Set<Authority> rolesForUser = m.getAuthorities();
                        //Insert the member if it doesn't exist
                        if(memberRepository.findByUsername(m.getUsername()) == null) {
                            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                            m.setPassword(bCryptPasswordEncoder.encode(m.getPassword()));
                            m = memberRepository.save(m);
                        
                            /* m is inserted in the persistence context *
                            * then check if the roles exist            *
                            *                                          */                     
                            if (rolesForUser != null && !rolesForUser.isEmpty()) {
                                for(Authority a : rolesForUser) {
                                    //load the role in the persistence context
                                    long id = authorityRepository.findIdByAuthorityName(a.getAuthorityName().toUpperCase());
                                    Authority tmp = authorityRepository.findById(id).get();
                                    m.getAuthorities().add(tmp);
                                }
                                m = memberRepository.save(m); //Update                            
                            }
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
