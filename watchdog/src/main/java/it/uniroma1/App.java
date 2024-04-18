package it.uniroma1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App 
{
    public static void main( String[] args ) throws IOException, TimeoutException{
            
        ExecutorService executor = Executors.newFixedThreadPool(2);
   
        
        executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f SYSLOG_FACILITY=4 -p 3"));
        executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f /usr/bin/sudo"));
        executor.shutdown();
        
        DirectExchange.declareExchange();

        Thread publish = new Thread(){
            @Override
            public void run() {
                try {
                    DirectExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        publish.start();

    }
}
