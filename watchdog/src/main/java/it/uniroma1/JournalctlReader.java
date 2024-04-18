package it.uniroma1;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import it.uniroma1.message.MessageProducer;


public class JournalctlReader implements Runnable {

    private String[] command;

    public JournalctlReader(String... command) throws Exception {
        // if(command == null || !(command.startsWith("journalctl") || command.startsWith("sudo journalctl")) ) {
        //     throw new Exception("Invalid journalctl");
        // }
        this.command = command;
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
                mp.sendMessage(line);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
