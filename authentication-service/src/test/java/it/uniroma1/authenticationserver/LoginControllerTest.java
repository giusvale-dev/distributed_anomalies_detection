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
    public void testJwtTokenVerification() {
          
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
          Claims claims = null;
          try {
              claims = jwtUtil.extractAllClaims(response.getBody());
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
    public void testAccessToSuperUserResource() {
        
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

        String token = response.getBody();
        assertNotNull(token);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> respEntity = restTemplate.exchange("http://localhost:" + port + "/api/private/superadmin_resource", HttpMethod.GET, requestEntity, String.class);

        assertEquals(HttpStatusCode.valueOf(200), respEntity.getStatusCode());
    }

    @Test
    public void testAccessToSystemAdministratorResource() {
        
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

        String token = response.getBody();
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
