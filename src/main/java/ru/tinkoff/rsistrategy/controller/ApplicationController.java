package ru.tinkoff.rsistrategy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.rsistrategy.cache.CandlesCache;
import ru.tinkoff.rsistrategy.cache.RSICache;
import ru.tinkoff.rsistrategy.cache.TradesCache;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final CandlesCache candlesCache;
    private final RSICache rsiCache;
    private final TradesCache tradesCache;

    @PostMapping("/rsi")
    public List<RSIStrategyConfig> start(@RequestBody List<RSIStrategyConfig> configs) {
        candlesCache.initCache(configs);
        return configs;
    }

    @GetMapping("/rsi")
    public Map<String, Map<Integer, BigDecimal>> getAllRsi() {
        return rsiCache.getCache();
    }

    @GetMapping("/rsi/trades")
    public List<TradesCache.Trade> getTrades() {
        return tradesCache.getCache();
    }
}
