package com.vaadin.tutorial.crm.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRunner.class);

    private final Revisar revisar;


    public ApplicationRunner(Revisar revisar) {
        this.revisar=revisar;
    }


    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        revisar.run();
    }
}