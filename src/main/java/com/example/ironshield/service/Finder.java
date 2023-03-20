package com.example.ironshield.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public String fileName="./file";

    @Scheduled(fixedRate = 1000000)
    public void readFile() {
        Boolean finded = false;
StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Runtime.getRuntime().exec(String.format("grep \"%s\" \"%s\"", PATTERN, fileName))
                        .getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
                sb.append(line);
                sb.append('\n');
                finded=true;

            }
            addLineToFile(sb);
            if(finded){
                mail.send("yann.jeanmaire@gmail.com","rapport log critique ","Bonjour yann ",new File("\\log.txt"));}

        } catch (IOException e) {
                logger.error(e.getMessage());
        }

    }
    public void addLineToFile(StringBuilder line) throws IOException {

            File file = new File("\\log.txt");
            try (FileWriter writer = new FileWriter(file,true)) {
                if (!file.exists()) { // if file does not exist, create it
                    file.createNewFile();
                }
                writer.write(line.toString());

            } catch (IOException e) {
              logger.warn(e.getMessage());
            }
        }
    }


