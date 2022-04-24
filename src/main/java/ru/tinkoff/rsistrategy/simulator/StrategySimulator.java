package ru.tinkoff.rsistrategy.simulator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.rsistrategy.cache.CandlesCache;
import ru.tinkoff.rsistrategy.cache.RSICache;
import ru.tinkoff.rsistrategy.model.CachedCandle;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;
import ru.tinkoff.rsistrategy.signal.RSISignalHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategySimulator {

    private final CandlesCache candlesCache;
    private final RSISignalHandler signalHandler;
    private final RSICache rsiCacheService;

    public void simulate(List<RSIStrategyConfig> configs) {
        candlesCache.collectHistoricalCandles(configs);
        var candles = candlesCache.getCache();
        for (RSIStrategyConfig config : configs) {
            for (String figi : config.getFigi()) {
                for (CachedCandle cachedCandle : candles.get(figi)) {
                    rsiCacheService.calculateRSI(figi, candles, config);
                    signalHandler.handle(cachedCandle.getClosePrice(), config, figi);
                }
            }
        }

    }
}
