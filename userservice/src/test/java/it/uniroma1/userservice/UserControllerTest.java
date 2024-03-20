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

package it.uniroma1.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.userservice.controllers.UserInsertModel;
import it.uniroma1.userservice.entities.ACK;
import it.uniroma1.userservice.entities.Role;
import it.uniroma1.userservice.entities.User;
import it.uniroma1.userservice.security.JwtUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;


    @Test
    public void insertUserControllerBySuperadmin() throws Exception {
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Role roleSuperadmin = new Role();
        roleSuperadmin.setAuthority("ROLE_SUPERADMIN");

        Role roleSystemAdministrator = new Role();
        roleSystemAdministrator.setAuthority("ROLE_SYSTEM_ADMINISTRATOR");
        
        // Create users with right roles
        User superadmin = new User();
        superadmin.setEmail("superadmin");
        superadmin.setUsername("superadmin");
        superadmin.setName("superadmin");
        superadmin.setSurname("superadmin");
        superadmin.setEnabled(true);

        Set<Role> superadminRoles = new HashSet<Role>();
        superadminRoles.add(roleSuperadmin);
        superadminRoles.add(roleSystemAdministrator);
        superadmin.setAuthorities(superadminRoles);
        
        //Simulate the login with a valid JWT signature
        String token = jwtUtil.generateToken(superadmin);
        assertNotNull(token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UserInsertModel userToInsert = new UserInsertModel(); //Simulate the object from the UI
        userToInsert.setEmail("john.doe@gmail.com");
        userToInsert.setName("John");
        userToInsert.setSurname("Doe");
        userToInsert.setUsername("john_doe");
        userToInsert.setPassword(bCryptPasswordEncoder.encode("HelloWorld!123"));
        userToInsert.setEnabled(true);
        userToInsert.setRoles(null);

        ObjectMapper ob = new ObjectMapper();
        String jsonBody = ob.writeValueAsString(userToInsert.toUser());

        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(jsonBody, headers);
        HttpEntity requestEntity = new HttpEntity<>(jsonBody, headers);


        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/user/insert", HttpMethod.POST, requestEntity, String.class);
        assertEquals(HttpStatusCode.valueOf(200), respEntity.getStatusCode());
        assertNotNull(respEntity.getBody());
        
    }

    @Test
    public void insertUserControllerBySystemAdministrator() throws Exception {
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Role roleSystemAdministrator = new Role();
        roleSystemAdministrator.setAuthority("ROLE_SYSTEM_ADMINISTRATOR");
        
        // Create users with right roles
        User superadmin = new User();
        superadmin.setEmail("superadmin");
        superadmin.setUsername("superadmin");
        superadmin.setName("superadmin");
        superadmin.setSurname("superadmin");
        superadmin.setEnabled(true);

        Set<Role> superadminRoles = new HashSet<Role>();
        superadminRoles.add(roleSystemAdministrator);
        superadmin.setAuthorities(superadminRoles);
        
        //Simulate the login with a valid JWT signature
        String token = jwtUtil.generateToken(superadmin);
        assertNotNull(token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UserInsertModel userToInsert = new UserInsertModel(); //Simulate the object from the UI
        userToInsert.setEmail("john.doe@gmail.com");
        userToInsert.setName("John");
        userToInsert.setSurname("Doe");
        userToInsert.setUsername("john_doe");
        userToInsert.setPassword(bCryptPasswordEncoder.encode("HelloWorld!123"));
        userToInsert.setEnabled(true);
        userToInsert.setRoles(null);

        ObjectMapper ob = new ObjectMapper();
        String jsonBody = ob.writeValueAsString(userToInsert.toUser());

        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(jsonBody, headers);
        HttpEntity requestEntity = new HttpEntity<>(jsonBody, headers);


        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/user/insert", HttpMethod.POST, requestEntity, String.class);
        assertEquals(HttpStatusCode.valueOf(401), respEntity.getStatusCode());
        
    }

    @Test
    public void insertUserControllerByUnauthorizedUser() throws Exception {
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Create users with right roles
        User superadmin = new User();
        superadmin.setEmail("superadmin");
        superadmin.setUsername("superadmin");
        superadmin.setName("superadmin");
        superadmin.setSurname("superadmin");
        superadmin.setEnabled(true);
        superadmin.setAuthorities(null);
        
        //Simulate the login with a valid JWT signature
        String token = jwtUtil.generateToken(superadmin);
        assertNotNull(token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UserInsertModel userToInsert = new UserInsertModel(); //Simulate the object from the UI
        userToInsert.setEmail("john.doe@gmail.com");
        userToInsert.setName("John");
        userToInsert.setSurname("Doe");
        userToInsert.setUsername("john_doe");
        userToInsert.setPassword(bCryptPasswordEncoder.encode("HelloWorld!123"));
        userToInsert.setEnabled(true);
        userToInsert.setRoles(null);

        ObjectMapper ob = new ObjectMapper();
        String jsonBody = ob.writeValueAsString(userToInsert.toUser());

        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(jsonBody, headers);
        HttpEntity requestEntity = new HttpEntity<>(jsonBody, headers);


        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/user/insert", HttpMethod.POST, requestEntity, String.class);
        assertEquals(HttpStatusCode.valueOf(401), respEntity.getStatusCode());
        
    }
    @Test
    public void editUserControllerByUnauthorizedUser() throws Exception {
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Create users with right roles
        User superadmin = new User();
        superadmin.setEmail("superadmin");
        superadmin.setUsername("superadmin");
        superadmin.setName("superadmin");
        superadmin.setSurname("superadmin");
        superadmin.setEnabled(true);
        superadmin.setAuthorities(null);
        
        //Simulate the login with a valid JWT signature
        String token = jwtUtil.generateToken(superadmin);
        assertNotNull(token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        UserEditModel userToEdit = new UserEditModel(); //Simulate the object from the UI
        String userId = "1";
        userToEdit.setEmail("john.doe@gmail.com");
        userToEdit.setName("John");
        userToEdit.setSurname("Doe");
        userToEdit.setUsername("john_doe");
        userToEdit.setPassword(bCryptPasswordEncoder.encode("HelloWorld!123"));
        userToEdit.setEnabled(true);
        userToEdit.setRoles(null);

        ObjectMapper ob = new ObjectMapper();
        String jsonBody = ob.writeValueAsString(userToEdit.toUser());

        HttpEntity requestEntity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/user/edit/" + userId, HttpMethod.POST, requestEntity, String.class);
        assertEquals(HttpStatusCode.valueOf(401), respEntity.getStatusCode());
        
    }


      /**
     * Simulate a message consumer, and sand back the message received to the
     * direct exchange
     * 
     * @param message The message received from the publisher
     * @return the loopback of the message sent by publisher
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     * @throws InvalidInputParameter 
     */
    @RabbitListener(queues = {"${queue.rabbitmq.listener.name}"})
    @SendTo("user_exchange/${binding.rabbitmq.key}")
    public String receiveMessage(String message) throws JsonMappingException, JsonProcessingException, InvalidInputParameter {


        ACK<User> replyMessage = new ACK<>();
        ObjectMapper om = new ObjectMapper();
        User u = (User) om.readValue(message, User.class);

        replyMessage.setMessage("Ok");
        replyMessage.setPayload(u);
        replyMessage.setSuccess(true);

        String response = om.writeValueAsString(replyMessage);

        //loopback the message to the exchange
        return response;
    }

}
