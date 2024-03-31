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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Authority;
import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.entities.models.UserUI;
import it.uniroma1.databaseservice.messaging.ACK;
import it.uniroma1.databaseservice.messaging.MessagePayload;
import it.uniroma1.databaseservice.messaging.OperationType;
import it.uniroma1.databaseservice.repositories.AuthorityRepository;
import it.uniroma1.databaseservice.repositories.MemberRepository;
import jakarta.transaction.Transactional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.core.DirectExchange;

@SpringBootTest
@ActiveProfiles("dev")
public class DatabaseListenerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private DirectExchange directExchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${binding.rabbitmq.key}")
    private String keyBinding;

    @Test
    public void testUserInsertWithoutRoles() throws JsonProcessingException {

        Member m = new Member();
        m.setName("Mario");
        m.setSurname("Rossi");
        m.setEmail("mario.rossi@gmail.com");
        m.setUsername("mariross");
        m.setPassword("password");
        m.setEnabled(true);

        // Create a message
        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.INSERT);
        mp.setUser(m);
        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), keyBinding,
                jsonMessage);
        assertNotNull(response);

        ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {
        });
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        assertEquals("Ok", ack.getMessage());
        assertTrue(ack.getPayload() != 0);

        // Check the user in the database
        Member loadFromDatabase = memberRepository.findById((long) ack.getPayload());
        assertNotNull(loadFromDatabase);
        assertEquals("Mario", loadFromDatabase.getName());
        assertEquals("Rossi", loadFromDatabase.getSurname());
        assertEquals("mario.rossi@gmail.com", loadFromDatabase.getEmail());
        assertEquals("mariross", loadFromDatabase.getUsername());
        assertTrue(m.getAuthorities() == null || m.getAuthorities().size() == 0);
    }

    @Test
    public void testUserInsertWithRoles() throws JsonProcessingException {

        List<Authority> roles = authorityRepository.findAll();
        assertNotNull(roles);

        Member m = new Member();
        m.setName("Giuseppe");
        m.setSurname("Verdi");
        m.setEmail("giuseppe.verdi@gmail.com");
        m.setUsername("giusverd");
        m.setPassword("password");
        m.setEnabled(true);

        Set<Authority> rolesForUser = new HashSet<Authority>();
        rolesForUser.addAll(roles);
        assertEquals(roles.size(), rolesForUser.size());
        m.setAuthorities(rolesForUser);

        // Create a message
        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.INSERT);
        mp.setUser(m);
        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), keyBinding,
                jsonMessage);
        assertNotNull(response);

        ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {
        });
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        assertEquals("Ok", ack.getMessage());
        assertTrue(ack.getPayload() != 0);

        // Check the user in the database
        Member loadFromDatabase = memberRepository.findById((long) ack.getPayload());
        assertNotNull(loadFromDatabase);
        assertEquals("Giuseppe", loadFromDatabase.getName());
        assertEquals("Verdi", loadFromDatabase.getSurname());
        assertEquals("giuseppe.verdi@gmail.com", loadFromDatabase.getEmail());
        assertEquals("giusverd", loadFromDatabase.getUsername());
        assertTrue(m.getAuthorities() != null && m.getAuthorities().size() == rolesForUser.size());
    }

    @Test
    @Transactional
    public void testDeleteUser() throws JsonProcessingException {

        // Create a message
        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.DELETE);
        Member userFromController = new Member();
        long id = memberRepository.findByUsername("disabledUser").getId();
        userFromController.setId(id);
        mp.setUser(userFromController);

        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), keyBinding,
                jsonMessage);
        assertNotNull(response);

        ACK<Object> ack = om.readValue(response, new TypeReference<ACK<Object>>() {});
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        
    }

    @Test
    public void testLoadUsersWithoutSearchString() throws JsonProcessingException {

        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.SEARCH);
        mp.setSearchString(null);
        mp.setUser(null);

        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), keyBinding,
                jsonMessage);
        assertNotNull(response);

        ACK<List<UserUI>> ack = om.readValue(response, new TypeReference<ACK<List<UserUI>>>() {});

        List<UserUI> listUnderTest = ack.getPayload();
        assertNotNull(listUnderTest);

        List<Member> trustedList = memberRepository.findAll();
        assertNotNull(trustedList);

        assertEquals(trustedList.size(), listUnderTest.size());

        //Check the lists containing the same elements
        List<Long> trustedIdList = new ArrayList<Long>();
        trustedList.forEach(m -> {
            assertNotNull(m);
            trustedIdList.add(m.getId());
        });

        List<Long> underTestIdList = new ArrayList<Long>();
        listUnderTest.forEach(m -> {
            assertNotNull(m);
            underTestIdList.add(m.getId());
        });

        assertTrue(underTestIdList.containsAll(trustedIdList));
    }

    @Test
    public void testLoadUsersWithSearchString() throws JsonProcessingException {

        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.SEARCH);
        mp.setSearchString("gr");
        mp.setUser(null);

        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), keyBinding,
                jsonMessage);
        assertNotNull(response);

        ACK<List<UserUI>> ack = om.readValue(response, new TypeReference<ACK<List<UserUI>>>() {});

        List<UserUI> listUnderTest = ack.getPayload();
        assertNotNull(listUnderTest);

        listUnderTest.forEach(m -> {
            assertNotNull(m);
            assertNotNull(m.getEmail());
            assertNotNull(m.getUsername());
            assertNotNull(m.getName());
            assertNotNull(m.getSurname());
            
            boolean containSearchedString = m.getEmail().contains("gr") ||
                                            m.getUsername().contains("gr") ||
                                            m.getName().contains("gr") ||
                                            m.getSurname().contains("gr");
            
            assertTrue(containSearchedString);

        });





        
    }

}