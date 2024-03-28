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


package it.uniroma1.databaseservice;



import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Authority;
import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.messaging.MessagePayload;
import it.uniroma1.databaseservice.messaging.OperationType;
import it.uniroma1.databaseservice.repositories.MemberRepository;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest
@ActiveProfiles("dev")
@Configuration
public class DatabaseListenerTest {

    @Value("${queue.rabbitmq.listener.name}")
    private String queueName;

    @Value("${binding.rabbitmq.key}")
    private String keyBinding;

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("user_exchange");
    }

    @Bean
    Binding bindingA(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(keyBinding);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MemberRepository memberRepository;

    

    @Test
    public void testUserInsert() throws JsonProcessingException {
        sendMessage();
        Member insertedUser = memberRepository.findByUsername("username");
        assertNotNull(insertedUser);

    }


    private String sendMessage() throws JsonProcessingException {
        
        MessagePayload payload = new MessagePayload();

        Member userToInset = new Member();
        userToInset.setEmail("test@email.com");
        userToInset.setUsername("username");
        userToInset.setName("name");
        userToInset.setSurname("surname");
        userToInset.setEnabled(true);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userToInset.setPassword(bCryptPasswordEncoder.encode("password"));

        Authority role = new Authority();
        role.setAuthorityName("ROLE_SUPERADMIN");
        
        Set<Authority> rolesForUser = new HashSet<Authority>();
        rolesForUser.add(role);
        userToInset.setAuthorities(rolesForUser);
        
        payload.setOperationType(OperationType.INSERT);
        payload.setUser(userToInset);

        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(payload);
        String response = (String) rabbitTemplate.convertSendAndReceive(exchange().getName(), keyBinding, jsonMessage);
        return response;
    }


}