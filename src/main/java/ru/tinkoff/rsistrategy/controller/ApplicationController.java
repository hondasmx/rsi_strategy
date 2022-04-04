package ru.tinkoff.rsistrategy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.rsistrategy.cache.CandlesCache;
import ru.tinkoff.rsistrategy.cache.RSICache;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final CandlesCache candlesCache;
    private final RSICache rsiCache;

    @PostMapping("/rsi")
    public RSIStrategyConfig start(@RequestBody RSIStrategyConfig config) {
        candlesCache.initCache(config);
        return config;
    }

    @GetMapping ("/rsi")
    public Map<String, Map<Integer, BigDecimal>> getAllRsi() {
        return rsiCache.getCache();
    }
}
