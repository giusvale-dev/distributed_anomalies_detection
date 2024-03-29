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
package it.uniroma1.authenticationserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;

import java.util.List;

import org.junit.jupiter.api.Test;
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

import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

import it.uniroma1.authenticationserver.entities.Member;
import it.uniroma1.authenticationserver.repositories.MemberRepository;
import it.uniroma1.authenticationserver.security.JwtUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class LoginControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testLoginSuccesfull() {

         //Load superadmin user from database
         Member superadmin = userRepository.findByUsername("superadmin");
         assertNotNull(superadmin);
 
        // Create a multimap to hold the named parameters
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("username", superadmin.getUsername());
        parameters.add("password", "HelloWolrd!123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/login",
                requestEntity,
                String.class);
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJwtTokenVerification() throws JsonMappingException, JsonProcessingException {
          
         //Load superadmin user from database
         Member superadmin = userRepository.findByUsername("superadmin");
         assertNotNull(superadmin);
 
        
          // Create a multimap to hold the named parameters
          MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
          parameters.add("username", superadmin.getUsername());
          parameters.add("password", "HelloWolrd!123");
  
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
  
          HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);
  
          // Make the POST request
          ResponseEntity<String> response = restTemplate.postForEntity(
                  "http://localhost:" + port + "/api/login",
                  requestEntity,
                  String.class);
          assertNotNull(response);
          assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
          ObjectMapper mapper = new ObjectMapper();
          JwtResponse jwt =  mapper.readValue(response.getBody(), JwtResponse.class);
          assertNotNull(jwt);
          Claims claims = null;
          try {
              claims = jwtUtil.extractAllClaims(jwt.toString());
              assertNotNull(claims);
              assertEquals(claims.get("username"), superadmin.getUsername());
              assertEquals(claims.get("enabled"), true);
              @SuppressWarnings("unchecked")
              List<String> rolesString = (List<String>) claims.get("roles");
              assertTrue(rolesString.contains("ROLE_SYSTEM_ADMINISTRATOR"));
              assertTrue(rolesString.contains("ROLE_SUPERADMIN"));
        } catch (UnsupportedEncodingException e) {
            assertFalse(true);
        }
    }

    @Test
    public void testLoginFailureForDisabledUser() {
       
        Member disabledUser = userRepository.findByUsername("disabledUser");
        assertNotNull(disabledUser);
        
        // Create a multimap to hold the named parameters
         MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
         parameters.add("username", disabledUser.getUsername());
         parameters.add("password", "HelloWolrd!123");
 
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 
         HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);
 
         // Make the POST request
         ResponseEntity<String> response = restTemplate.postForEntity(
                 "http://localhost:" + port + "/api/login",
                 requestEntity,
                 String.class);
         assertNotNull(response);
         assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
         assertEquals("Username/Password not valid", response.getBody());

    }

    @Test
    public void testAccessToSuperUserResource() throws JsonMappingException, JsonProcessingException {
        
        //Load superadmin user from database
        Member superadmin = userRepository.findByUsername("superadmin");
        assertNotNull(superadmin);

        // Create a multimap to hold the named parameters
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("username", superadmin.getUsername());
        parameters.add("password", "HelloWolrd!123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/login",
                requestEntity,
                String.class);

        ObjectMapper mapper = new ObjectMapper();
        JwtResponse jwt =  mapper.readValue(response.getBody(), JwtResponse.class);
        assertNotNull(jwt);

        String token = jwt.toString();
        assertNotNull(token);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/private/superadmin_resource", HttpMethod.GET, requestEntity, String.class);

        assertEquals(HttpStatusCode.valueOf(200), respEntity.getStatusCode());
    }

    @Test
    public void testAccessToSystemAdministratorResource() throws JsonMappingException, JsonProcessingException {
        
        //Load superadmin user from database
        Member superadmin = userRepository.findByUsername("superadmin");
        assertNotNull(superadmin);

        // Create a multimap to hold the named parameters
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("username", superadmin.getUsername());
        parameters.add("password", "HelloWolrd!123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/login",
                requestEntity,
                String.class);

        ObjectMapper mapper = new ObjectMapper();
        JwtResponse jwt =  mapper.readValue(response.getBody(), JwtResponse.class);
        assertNotNull(jwt);
        
        String token = jwt.toString();
        assertNotNull(token);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/private/system_administrator_resource", HttpMethod.GET, requestEntity, String.class);

        assertEquals(HttpStatusCode.valueOf(200), respEntity.getStatusCode());
    }

    @Test
    public void denyAccessToSuperadminResource() {
        //Create a system administrator user
        Member systemAdminUser = userRepository.findByUsername("systemAdminUser");
        assertNotNull(systemAdminUser);
        
        // Create a multimap to hold the named parameters
         MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
         parameters.add("username", systemAdminUser.getUsername()); //Doesn't have right role but is able to login
         parameters.add("password", "HelloWolrd!123");
 
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 
         HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);
 
         // Make the POST request
         ResponseEntity<String> response = restTemplate.postForEntity(
                 "http://localhost:" + port + "/api/login",
                 requestEntity,
                 String.class);
 
         String token = response.getBody();
         assertNotNull(token);
 
         headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         headers.setBearerAuth(token);
 
         requestEntity = new HttpEntity<>(headers);
 
         ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/private/superadmin_resource", HttpMethod.GET, requestEntity, String.class);
 
         assertEquals(HttpStatusCode.valueOf(401), respEntity.getStatusCode());

    }

}
