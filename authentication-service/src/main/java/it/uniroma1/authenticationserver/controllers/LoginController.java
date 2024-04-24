/**
 * MIT No Attribution
 *
 * Copyright 2024 Giuseppe Valente, Antonio Cipriani, Natalia Mucha, Md Anower Hossain
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
package it.uniroma1.authenticationserver.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.authenticationserver.entities.Authority;
import it.uniroma1.authenticationserver.entities.Member;
import it.uniroma1.authenticationserver.security.CustomAuth;
import it.uniroma1.authenticationserver.security.JwtUtil;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class LoginController {

    @Autowired
    private CustomAuth customAuth;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Check if the user is authenticated or not
     * 
     * @param username The username
     * @param password The password
     * @return the JWT Token if the user is authenticated
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {

        try {
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authentication = customAuth.authenticate(authentication);
            if(authentication != null) {
              Member u = new Member();
              u.setUsername(authentication.getName());
              Set<Authority> roles = new HashSet<Authority>();
              for(GrantedAuthority ga : authentication.getAuthorities()) {
                roles.add((Authority) ga);
              }
              u.setEnabled(true); //The authentication is done, the user is enabled to login
              u.setAuthorities(roles);
              String token = jwtUtil.generateToken(u);
              if(token != null) {
                ObjectMapper mapper = new ObjectMapper();
                TokenModel t = new TokenModel(token);
                return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(t));
              } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem during the generation of JWT token");
              }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username/Password not valid");
            }
        
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/api/public")
    public String publicEndpoint() {
        return "Public endpoint test purpose";
    }

    @GetMapping("/api/private/superadmin_resource")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String privateSuperadminEndpoint() {
        return "Superadmin resource";
    }

    @GetMapping("/api/private/system_administrator_resource")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public String privateSystemAdministratorEndpoint() {
        return "System Administrator resource";
    }
}
