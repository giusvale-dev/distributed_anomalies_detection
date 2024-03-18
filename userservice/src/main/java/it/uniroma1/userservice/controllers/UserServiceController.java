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

package it.uniroma1.userservice.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.userservice.entities.ACK;
import it.uniroma1.userservice.entities.User;
import it.uniroma1.userservice.messaging.MessageProducer;

@RestController
@Validated
public class UserServiceController {

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping("/api/user/hello")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<String> protectedResourceExample() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body("Hello!");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/api/user/insert")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> insertUser(@Valid @RequestBody UserInsertModel userModel) {
        try {
            User u = userModel.toUser();
            String response = messageProducer.sendMessage(u);
            if (response != null) {
                //ACK RECEIVED
                ObjectMapper om = new ObjectMapper();
                ACK<User> ack = om.readValue(response, new TypeReference<ACK<User>>() {});
                if (ack != null) {
                    if (ack.isSuccess()) {
                        if (ack.getPayload() != null) {
                            om = new ObjectMapper();
                            String bodyResponse = om.writeValueAsString(u);
                            return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);    
                        } else {
                            return ResponseEntity.status(HttpStatus.OK).body("OK");    
                        } 
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ack.getMessage());
                    }
                } else {
                    //ERROR
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Can't complete the operation");
                }
            } else {
                //REQUEST NOT PERFORMED
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("At the moment is not possible satisy the operation request");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
