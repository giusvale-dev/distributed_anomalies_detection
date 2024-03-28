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
import jakarta.transaction.Transactional;

@Service
public class MessageListener {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @RabbitListener(queues = {"${queue.rabbitmq.listener.name}"})
    @SendTo("user_exchange/${binding.rabbitmq.key}")
    @Transactional
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
                        //Insert the member if it doesn't exist
                        if(memberRepository.findByUsername(m.getUsername()) == null) {
                            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                            m.setPassword(bCryptPasswordEncoder.encode(m.getPassword()));
                            if(m.getAuthorities() != null && !m.getAuthorities().isEmpty()) {
                                Authority tmp = null;
                                for(Authority a : m.getAuthorities()) {
                                    if(a != null) {
                                        tmp = authorityRepository.findByAuthorityName(a.getAuthorityName().toUpperCase());
                                    }
                                    if(tmp != null) {
                                        m.getAuthorities().add(a);
                                    }
                                }
                            }
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
