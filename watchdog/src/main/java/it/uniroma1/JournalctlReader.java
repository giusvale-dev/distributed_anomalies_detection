package it.uniroma1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalctlReader {

    static void runJournalctlCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                Anomaly anomaly = parseLineToAnomaly(line);
                System.out.println(anomaly);
            }

            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Anomaly parseLineToAnomaly(String line) {
        String datetime = line.substring(0, line.indexOf(' '));
        String details = line.substring(line.indexOf(' ') + 1);
        return new Anomaly(datetime, details);
    }
}
