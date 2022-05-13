package com.bot.chomikuj.botchomikuj.bot.controller;

import com.bot.chomikuj.botchomikuj.bot.controller.dto.SecurityDataDTO;
import com.bot.chomikuj.botchomikuj.bot.controller.dto.WaitPeriodDTO;
import com.bot.chomikuj.botchomikuj.bot.service.ChomikujBotService;
import com.bot.chomikuj.botchomikuj.bot.service.ChomikujConfiguration;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/configuration")
public class ConfigurationController {

    private final ChomikujBotService service;
    private final ChomikujConfiguration configuration;

    @PutMapping("/period")
    public void changeWaitTime(@Validated @RequestBody WaitPeriodDTO dto) {
        log.info("Set configuration: {}", dto.toString());
        configuration.setNumberOfDownloadHamster(dto.numberOfDownloadHamster());
        configuration.setWaitEachGetProfilePeriod(dto.waitEachGetProfilePeriod());
        configuration.setMinWaitEachAddCommendPeriod(dto.minWaitEachAddCommendPeriod());
        configuration.setMaxWaitEachAddCommendPeriod(dto.maxWaitEachAddCommendPeriod());
    }

    @PutMapping("/security")
    public void changeSecurity(@Validated @RequestBody SecurityDataDTO dto) {
        log.info("Set configuration: {}", dto.toString());
        if (StringUtils.hasLength(dto.session())) {
            configuration.setSession(dto.session());
        }
        if (StringUtils.hasLength(dto.token())) {
            configuration.setToken(dto.token());
        }
    }

    @PutMapping("/message")
    public void changeMessage(@RequestBody String dto) {
        log.info("Set message: {}", dto);
        configuration.setCommand(dto);
    }

    @PostMapping("/lunch")
    public void lunchBot() throws UnirestException, InterruptedException {
        log.info("Lunch bot!");
        service.startBot();
    }
}
