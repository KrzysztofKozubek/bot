package com.bot.chomikuj.botchomikuj.bot.service;

import com.bot.chomikuj.botchomikuj.bot.service.model.Hamster;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChomikujBotService {

    private final ChomikujClient client;
    private final ChomikujConfiguration configuration;

    public void startBot() throws UnirestException, InterruptedException {
        int numberOfDownloadHamster = configuration.getNumberOfDownloadHamster();
        log.info("Get {} profiles.", numberOfDownloadHamster);
        Collection<String> recommendHamsters = client.getListOfRecommendHamster(numberOfDownloadHamster);
        for (String hamster : recommendHamsters) {
            Duration waitEachGetProfilePeriod = Duration.ofMillis(configuration.getWaitEachGetProfilePeriod());
            log.info("Wait {} on get profile", waitEachGetProfilePeriod);
            ChomikujClient.randomSleep(waitEachGetProfilePeriod, waitEachGetProfilePeriod);

            log.info(LocalDateTime.now().toString());
            Hamster profile = client.getProfile(hamster);
            log.info(profile.name());
            if (!profile.containsOurCommend()) {
                Duration minWaitEachAddCommendPeriod = Duration.ofMillis(configuration.getMinWaitEachAddCommendPeriod());
                Duration maxWaitEachAddCommendPeriod = Duration.ofMillis(configuration.getMaxWaitEachAddCommendPeriod());
                log.info("Wait ({} - {}) on try add comment to profile", minWaitEachAddCommendPeriod, maxWaitEachAddCommendPeriod);
                ChomikujClient.randomSleep(minWaitEachAddCommendPeriod, maxWaitEachAddCommendPeriod);


                String command = configuration.getCommand();
                log.info("Try add commend: {}", command);
                client.addCommend(profile, String.format(command, hamster));
            }
            log.info("");
        }
    }
}
