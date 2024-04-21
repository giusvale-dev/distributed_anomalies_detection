package it.uniroma1;

public class App 
{
    public static void main( String[] args ) throws Exception{
            
        String[] arg = {"journalctl", "-f", "/usr/bin/sudo"};
        JournalctlReader j1 = new JournalctlReader(arg);
        Thread t1 = new Thread(j1);
        t1.start();
        arg = new String[] {"journalctl", "-u", "sshd.service", "-f"};
        JournalctlReader j2 = new JournalctlReader(arg);
        Thread t2 = new Thread(j2);
        t2.start();

        
    }

}
