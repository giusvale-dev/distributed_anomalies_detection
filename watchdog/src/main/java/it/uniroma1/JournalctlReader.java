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
package it.uniroma1;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma1.message.MessagePayload;
import it.uniroma1.message.MessageProducer;
import it.uniroma1.message.OperationType;
import it.uniroma1.models.Anomaly;


public class JournalctlReader implements Runnable {

    private String[] command;
    private String ipAddress;
    private String hostname;

    public JournalctlReader(String... command) throws Exception {
        this.command = command;
        this.ipAddress = Inet4Address.getLocalHost().getHostAddress();
        this.hostname = Inet4Address.getLocalHost().getHostName();
    }

    @Override
    public void run() {
        
        try {

            ProcessBuilder pb = new ProcessBuilder(this.command);    
        
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            MessageProducer mp = new MessageProducer();

            String line;
            while ((line = br.readLine()) != null) {

                Anomaly anomaly = new Anomaly();
                anomaly.setDatetime(new Date());
                anomaly.setHostname(this.hostname);
                anomaly.setIpAddress(this.ipAddress);
                anomaly.setDetails(line);

                MessagePayload messagePayload = new MessagePayload();
                messagePayload.setData(anomaly);
                messagePayload.setOperationType(OperationType.INSERT);

                ObjectMapper om = new ObjectMapper();
                String jsonMessage = om.writeValueAsString(messagePayload);

                mp.sendMessage(jsonMessage);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
