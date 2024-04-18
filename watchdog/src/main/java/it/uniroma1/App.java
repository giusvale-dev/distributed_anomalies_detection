package it.uniroma1;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App 
{
    public static void main( String[] args ) throws Exception{
            
        
        
        String[] arg = {"journalctl", "-f", "/usr/bin/sudo"};
        JournalctlReader j1 = new JournalctlReader(arg);
        Thread t1 = new Thread(j1);
        t1.start();
        
        arg = new String[] {"journalctl", "-f", "SYSLOG_FACILITY=4", "-p", "3"};
        JournalctlReader j2 = new JournalctlReader(arg);
        Thread t2 = new Thread(j2);
        t2.start();

        // ExecutorService executor = Executors.newFixedThreadPool(2);
   
        
        // executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f SYSLOG_FACILITY=4 -p 3"));
        // executor.submit(() -> JournalctlReader.runJournalctlCommand("journalctl -f /usr/bin/sudo"));
        // executor.shutdown();
        
        // DirectExchange.declareExchange();

        // Thread publish = new Thread(){
        //     @Override
        //     public void run() {
        //         try {
        //             DirectExchange.publishMessage();
        //         } catch (IOException | TimeoutException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // };

        // publish.start();

    }
}
