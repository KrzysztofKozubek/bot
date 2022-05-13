package com.bot.chomikuj.botchomikuj.bot.controller.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record WaitPeriodDTO(
        int numberOfDownloadHamster,    // default 50
        int waitEachGetProfilePeriod,   // default 1_000 * 60 * 5
        int minWaitEachAddCommendPeriod,// default 1_000 * 60 * 10
        int maxWaitEachAddCommendPeriod // default 1_000 * 60 * 12
) {
}
