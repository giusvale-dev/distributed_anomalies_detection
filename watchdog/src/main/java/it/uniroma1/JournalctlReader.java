package it.uniroma1;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.Inet4Address;

import it.uniroma1.message.MessageProducer;


public class JournalctlReader implements Runnable {

    private String[] command;
    private String ipAddress;

    public JournalctlReader(String... command) throws Exception {
        this.command = command;
        this.ipAddress = Inet4Address.getLocalHost().getHostAddress();
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
                mp.sendMessage(this.ipAddress + ":" + line);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
