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
package it.uniroma1.userservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.userservice.InvalidInputParameter;

@Service
public class MessageProducer {

    Logger logger = LoggerFactory.getLogger(MessageProducer.class);

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

    public String sendMessage(MessagePayload payload) throws InvalidInputParameter, JsonProcessingException {
        
        if(payload != null) {     
            
            ObjectMapper om = new ObjectMapper();
            String jsonMessage = om.writeValueAsString(payload);
            String response = (String) rabbitTemplate.convertSendAndReceive(userExchange.getName(), keyBinding, jsonMessage);
            return response;
            
        }
        return null;
    }

    public String sendAnomalyMessage(AnomalyMessagePayload payload) throws JsonProcessingException  {

        if(payload != null) {     
            ObjectMapper om = new ObjectMapper();
            String jsonMessage = om.writeValueAsString(payload);
            String response = (String) rabbitTemplate.convertSendAndReceive(anomalyExchange.getName(), keyBindingAnomaly, jsonMessage);
            return response;
        }
        return null;
    }
}
