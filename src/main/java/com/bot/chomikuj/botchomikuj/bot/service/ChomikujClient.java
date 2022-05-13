package com.bot.chomikuj.botchomikuj.bot.service;

import com.bot.chomikuj.botchomikuj.bot.service.model.Hamster;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChomikujClient {

    private final ChomikujConfiguration configuration;

    private static final Pattern PATTERN_OF_RECOMMEND_HAMSTER = Pattern.compile("href=\"(.*?)\"");
    private static final Pattern PATTERN_OF_BSKR = Pattern.compile("input id=\"bskr\" name=\"bskr\" type=\"hidden\" value=\"(.*?)\"");
    private static final Pattern PATTERN_OF_REQUEST_VERIFICATION_TOKEN = Pattern.compile("\"__RequestVerificationToken\" type=\"hidden\" value=\"(.*?)\"");
    private static final Pattern PATTERN_OF_CONTAINS_OUT_COMMAND = Pattern.compile("a href=\"/Fire-Wind\" title=\"Fire-Wind\"");

    public static void randomSleep(Duration min, Duration max) throws InterruptedException {
        long minV = min.toMillis();
        long maxV = max.toMillis();
        maxV = minV >= maxV ? minV + 1 : maxV;
        int i = ThreadLocalRandom.current().nextInt((int) minV, (int) maxV);
        Thread.sleep(i);
    }

    public Hamster getProfile(String hamsterName) throws UnirestException {
        String url = String.format("https://chomikuj.pl/%s", hamsterName);
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get(url)
                .header("cookie", getCOOKIE())
                .asString();
        String body = response.getBody();
        // bskr
        Matcher matcher = PATTERN_OF_BSKR.matcher(body);
        String bskr = null;
        while (matcher.find()) {
            bskr = matcher.group(1);
        }
        // token
        matcher = PATTERN_OF_REQUEST_VERIFICATION_TOKEN.matcher(body);
        String token = null;
        while (matcher.find()) {
            token = matcher.group(1);
        }
        // command
        matcher = PATTERN_OF_CONTAINS_OUT_COMMAND.matcher(body);
        int counterOfOutLinks = 0;
        while (matcher.find()) {
            counterOfOutLinks++;
        }

        return new Hamster(hamsterName, bskr, token, counterOfOutLinks > 1);
    }

    public Collection<String> getListOfRecommendHamster(int number) throws UnirestException {
        String url = String.format("https://chomikuj.pl/action/LastAccounts/RecommendedAccounts?itemsCount=%s", number);
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get(url)
                .header("Cookie", "guid=080c5c51-b467-450a-9765-1dc428233fbd; rcid=3")
                .asString();

        String body = response.getBody();
        Matcher matcher = PATTERN_OF_RECOMMEND_HAMSTER.matcher(body);
        Set<String> result = new HashSet<>();
        while (matcher.find()) {
            result.add(matcher.group(1).substring(1));
        }
        return result;
    }

    public String addCommend(Hamster hamster, String commend) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://chomikuj.pl/action/ChomikChat/SendMessage")
                .header("authority", "chomikuj.pl")
                .header("accept", "*/*")
                .header("content-type", "application/x-www-form-urlencoded")
                .header("cookie", getCOOKIE())
                .header("origin", "https://chomikuj.pl")
                .header("referer", "https://chomikuj.pl/" + hamster.name())
                .header("sec-ch-ua-mobile", "?0")
                .header("user-agent", configuration.getUserAgent())
                .header("x-requested-with", "XMLHttpRequest")
                .field("TargetChomikName", hamster.name())
                .field("Mode", "Last")
                .field("bskr", hamster.bskr())
                .field("Message", commend)
                .field("__RequestVerificationToken", hamster.token())
                .asString();
        return response.getBody();
    }

    private static final String COOKIE = """
            ChomikSession=%s;
            __RequestVerificationToken_Lw__=%s;
            """;

    private String getCOOKIE() {
        return String.format(COOKIE, configuration.getSession(), configuration.getToken());
    }
}