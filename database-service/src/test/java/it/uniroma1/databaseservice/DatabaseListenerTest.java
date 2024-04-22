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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.databaseservice.entities.Anomaly;
import it.uniroma1.databaseservice.entities.Authority;
import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.entities.models.AnomalyModel;
import it.uniroma1.databaseservice.entities.models.UserUI;
import it.uniroma1.databaseservice.messaging.ACK;
import it.uniroma1.databaseservice.messaging.GenericMessagePayload;
import it.uniroma1.databaseservice.messaging.MessagePayload;
import it.uniroma1.databaseservice.messaging.OperationType;
import it.uniroma1.databaseservice.repositories.AnomalyRepository;
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
    private AnomalyRepository anomalyRepository;

    @Autowired
    @Qualifier("userExchange")
    private DirectExchange userExchange;

    @Autowired
    @Qualifier("anomalyExchange")
    private DirectExchange anomalyExchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${binding.rabbitmq.key}")
    private String keyBinding;

    @Value("${binding.rabbitmq.anomaly.key}")
    private String keyBindingAnomaly;

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

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding,
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

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding,
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

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding,
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

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding,
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

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding,
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

    @Test
    @Transactional
    public void testUpdateMember() throws JsonMappingException, JsonProcessingException {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        
        long idMember = memberRepository.loadMemberIdByUsername("systemAdminUser");

        Member m = new Member();
        m.setId(idMember);
        m.setName("EditedName");
        m.setSurname("EditedSurname");
        m.setEmail("edited_email@gmail.com");
        m.setUsername("editedUsername");
        m.setPassword("edited_password");
        m.setEnabled(false);
        m.setAuthorities(null);
        // Create a message
        MessagePayload mp = new MessagePayload();
        mp.setOperationType(OperationType.UPDATE);
        mp.setUser(m);
        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding, jsonMessage);
        assertNotNull(response);

        ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {});
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        assertEquals("Ok", ack.getMessage());
        assertEquals(ack.getPayload(), idMember);

        //Load the user from the database
        Member editedUser = memberRepository.findById(idMember);
        assertNotNull(editedUser);
        assertEquals("EditedName", editedUser.getName());
        assertEquals("EditedSurname", editedUser.getSurname());
        assertEquals("edited_email@gmail.com", editedUser.getEmail());
        assertEquals("systemAdminUser", editedUser.getUsername()); //username does not edited
        assertEquals(false, editedUser.getEnabled());
        assertTrue(editedUser.getAuthorities() == null || editedUser.getAuthorities().size() == 0);
        assertTrue(bCryptPasswordEncoder.matches("edited_password", editedUser.getPassword()));
    
    }

    @Test
    public void test_anomaly_insert() throws JsonProcessingException {

        GenericMessagePayload mp = new GenericMessagePayload();
        mp.setOperationType(OperationType.INSERT);

        AnomalyModel model = new AnomalyModel();
        model.setDatetime(new Date());
        String detail = "apr 21 18:33:51 archlinux-vm sudo[4703]: pam_unix(sudo:session): session opened for user root(uid=0) by giusvale(uid=1000)";
        model.setDetails(detail);
        model.setHostname("hostname");
        model.setIpAddress("10.0.0.3");

        mp.setData(model);

        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
        assertNotNull(response);

        ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {});
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        assertNotEquals(0, ack.getPayload());

    }

    @Test
    public void test_anomaly_read() throws JsonProcessingException {

        GenericMessagePayload mp = new GenericMessagePayload();
        String detail = "apr 21 18:33:51 archlinux-vm sudo[4703]: pam_unix(sudo:session): session opened for user root(uid=0) by giusvale(uid=1000)";
        
        for(int i = 0; i < 100; i++) {

            mp.setOperationType(OperationType.INSERT);
            AnomalyModel model = new AnomalyModel();
            model.setDatetime(new Date());
            model.setDetails(i + " " + detail);
            model.setHostname("hostname");
            model.setIpAddress("10.0.0.3");
            mp.setData(model);
            ObjectMapper om = new ObjectMapper();
            String jsonMessage = om.writeValueAsString(mp);

            String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
            assertNotNull(response);

            ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {});
            assertNotNull(ack);
            assertEquals(true, ack.isSuccess());
            assertNotEquals(0, ack.getPayload());

        }

        mp = new GenericMessagePayload();
        mp.setOperationType(OperationType.SEARCH);
        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(mp);

        String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
        assertNotNull(response);

        ACK<List<Anomaly>> ack = om.readValue(response, new TypeReference<ACK<List<Anomaly>>>() {});
        assertNotNull(ack);
        assertEquals(true, ack.isSuccess());
        List<Anomaly> lst = ack.getPayload();
        assertNotNull(lst);
        lst.forEach((anomaly) -> {
            assertNotNull(anomaly);
            assertEquals("hostname", anomaly.getHostname());
            assertEquals("10.0.0.3", anomaly.getIpAddress());
            assertTrue(anomaly.getDescription().contains(detail));
        });
        
    }

    @Test
    public void test_anomaly_reset_to_green() throws JsonProcessingException {

        GenericMessagePayload mp = new GenericMessagePayload();
        String detail = "apr 21 18:33:51 archlinux-vm sudo[4703]: pam_unix(sudo:session): session opened for user root(uid=0) by giusvale(uid=1000)";
        
        List<Long> insertedAnomaliesId = new ArrayList<Long>();

        //insert anomalies
        for(int i = 0; i < 100; i++) {

            mp.setOperationType(OperationType.INSERT);
            AnomalyModel model = new AnomalyModel();
            model.setDatetime(new Date());
            model.setDetails(new Date().toString() + " " + detail);
            model.setHostname("hostname");
            model.setIpAddress("10.0.0.3");
            mp.setData(model);
            ObjectMapper om = new ObjectMapper();
            String jsonMessage = om.writeValueAsString(mp);

            String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
            assertNotNull(response);

            ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {});
            assertNotNull(ack);
            assertEquals(true, ack.isSuccess());
            assertNotEquals(0, ack.getPayload());
            insertedAnomaliesId.add(ack.getPayload());

        }

        //reset to green the anomalies
        for(long id : insertedAnomaliesId) {

            mp = new GenericMessagePayload();
            mp.setOperationType(OperationType.UPDATE);
            
            AnomalyModel am = new AnomalyModel();
            am.setId(id);

            mp.setData(am);

            ObjectMapper om = new ObjectMapper();
            String jsonMessage = om.writeValueAsString(mp);
            String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
            assertNotNull(response);
    
            ACK<Long> ack = om.readValue(response, new TypeReference<ACK<Long>>() {});
            assertNotNull(ack);
            assertEquals(true, ack.isSuccess());
        }

        //check if the anomalies are toggled to true in the done field
        for(long id : insertedAnomaliesId) {
            Anomaly a = anomalyRepository.findById(id).orElse(null);
            assertNotNull(a);
            assertEquals(true, a.getDone());
        }

    
    }


}