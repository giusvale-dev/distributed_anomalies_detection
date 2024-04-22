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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.userservice.entities.ACK;
import it.uniroma1.userservice.entities.AnomalyModel;
import it.uniroma1.userservice.entities.OperationType;
import it.uniroma1.userservice.messaging.AnomalyMessagePayload;
import it.uniroma1.userservice.messaging.MessageProducer;

@RestController
@Validated
public class AnomalyController {

    Logger logger = LoggerFactory.getLogger(UserServiceController.class);

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping("/api/anomaly/view")
    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<String> loadAllUnresolvedAnomalies() {
        try {
            logger.info("loadAllUnresolvedAnomalies()");
            AnomalyMessagePayload mp = new AnomalyMessagePayload();
            mp.setOperationType(OperationType.SEARCH);
            String response = messageProducer.sendAnomalyMessage(mp);
            if(response != null) {
                ObjectMapper om = new ObjectMapper();
                ACK<Object> ack = om.readValue(response, new TypeReference<ACK<Object>>() {});
                if (ack != null) {
                    if (ack.isSuccess()) {
                        om = new ObjectMapper();
                        String bodyResponse = om.writeValueAsString(ack.getPayload());
                        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);    
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ack.getMessage());
                    }
                } else {
                    //ERROR
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Can't complete the operation");
                }

            } else {
                //REQUEST NOT PERFORMED
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("At the moment is not possible satisfy the operation request");
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/api/anomaly/fix")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<String> resetToGreen(@PathVariable long id) {
        try {
            logger.info("resetToGreen()");
            AnomalyMessagePayload mp = new AnomalyMessagePayload();
            mp.setOperationType(OperationType.UPDATE);
            AnomalyModel model = new AnomalyModel();
            model.setId(id);
            String response = messageProducer.sendAnomalyMessage(mp);
            if(response != null) {
                ObjectMapper om = new ObjectMapper();
                ACK<Object> ack = om.readValue(response, new TypeReference<ACK<Object>>() {});
                if (ack != null) {
                    if (ack.isSuccess()) {
                        return ResponseEntity.status(HttpStatus.OK).body("{ 'response' : 'OK'}");    
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ack.getMessage());
                    }
                } else {
                    //ERROR
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Can't complete the operation");
                }

            } else {
                //REQUEST NOT PERFORMED
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("At the moment is not possible satisfy the operation request");
            }
           
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
