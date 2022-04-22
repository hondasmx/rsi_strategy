package ru.tinkoff.rsistrategy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.rsistrategy.cache.CandlesCache;
import ru.tinkoff.rsistrategy.cache.TradesCache;
import ru.tinkoff.rsistrategy.simulator.StrategySimulator;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;
import ru.tinkoff.rsistrategy.service.SandboxAccountService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final CandlesCache candlesCache;
    private final TradesCache tradesCache;
    private final SandboxAccountService sandboxAccountService;

    private final StrategySimulator simulator;

    @PostMapping("/rsi")
    public List<RSIStrategyConfig> start(@RequestBody List<RSIStrategyConfig> configs) {
        candlesCache.initCache(configs);
        return configs;
    }

    @GetMapping("/portfolio")
    public BigDecimal getPortfolio() {
        return sandboxAccountService.totalAmountOfFunds();
    }

    @PostMapping("/test/rsi")
    public List<RSIStrategyConfig> startTest(@RequestBody List<RSIStrategyConfig> configs) {
        simulator.simulate(configs);
        return configs;
    }

    @GetMapping("/test/portfolio")
    public BigDecimal getPortfolioTest() {
        return sandboxAccountService.totalAmountOfFunds();
    }

    @GetMapping("/rsi/trades")
    public List<TradesCache.Trade> getTrades() {
        return tradesCache.getCache();
    }
}
