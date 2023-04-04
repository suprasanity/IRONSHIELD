package com.example.ironshield.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
@EnableScheduling
public class Finder {

    @Autowired
    Mail mail;

    Logger logger = LoggerFactory.getLogger(Finder.class);
    public static final String PATTERN = "sshd.*session opened for user.*";

    public String fileName="auth.log";
    @Scheduled(cron = "0 0 0 ? * MON")
    public void readFile() {
        boolean found = false;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("grep", "-a", PATTERN, fileName);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info(line);
                    writer.println(line);
                    found = true;
                }
            }
            if (found) {
                mail.send("yann.jeanmaire@gmail.com", "rapport log critique", "Bonjour yann",
                        new File("output.txt"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}


