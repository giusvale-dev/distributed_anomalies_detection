package it.uniroma1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App 
{
    public static void main( String[] args ) throws IOException, TimeoutException{

        ExecutorService executor = Executors.newFixedThreadPool(2);
   
        // Launch both monitoring tasks
        //executor.submit(() -> runJournalctlCommand("journalctl -f SYSLOG_FACILITY=4 -p 3"));
        //executor.submit(() -> runJournalctlCommand("journalctl -f /usr/bin/sudo"));

        executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f SYSLOG_FACILITY=4 -p 3"));
        executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f /usr/bin/sudo"));
        System.out.println();
        executor.shutdown();


        //Creating new Direct Exchange and Key Binding in the host application
        
        DirectExchange.declareQueues();
        DirectExchange.declareExchange();
        DirectExchange.declareBindings();

        Thread subscribe = new Thread(){
            @Override
            public void run() {
                try {
                    DirectExchange.subscribeMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

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

        subscribe.start();
        publish.start();
        


    }
}
