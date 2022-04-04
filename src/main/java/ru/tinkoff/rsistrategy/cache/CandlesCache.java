package ru.tinkoff.rsistrategy.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import ru.tinkoff.piapi.contract.v1.SubscriptionStatus;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;
import ru.tinkoff.rsistrategy.model.CachedCandle;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;
import ru.tinkoff.rsistrategy.service.SdkService;
import ru.tinkoff.rsistrategy.signal.RSISignalHandler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CandlesCache {

    @Getter
    private final Map<String, TreeSet<CachedCandle>> cache = new HashMap<>();

    private final SdkService sdkService;
    private final RSISignalHandler signalHandler;
    private final RSICache rsiCacheService;

    private Set<CachedCandle> collectCandlesFor1Day(MarketDataService marketDataService, String figi, Instant endDate) {
        var startDate = endDate.minus(1, ChronoUnit.DAYS);
        return marketDataService.getCandlesSync(figi, startDate, endDate, CandleInterval.CANDLE_INTERVAL_5_MIN)
                .stream()
                .map(CachedCandle::ofHistoricCandle)
                .collect(Collectors.toSet());
    }


    public void initCache(RSIStrategyConfig config) {
        var figi = config.getFigi();
        var mdService = sdkService.getInvestApi().getMarketDataService();
        var endDate = OffsetDateTime.now().toInstant();
        log.info("init candles for figi " + figi);
        log.info("end date for candles {}", OffsetDateTime.ofInstant(endDate, ZoneId.of("UTC")));

        var candles = new TreeSet<CachedCandle>(Comparator.comparingLong(candle -> candle.getTimestamp().getSeconds()));
        cache.put(figi, candles);

        var candlesDays = config.getInitialCandlesSizeDays();
        for (var i = 0; i < candlesDays; i++) {
            var collectedCandles = collectCandlesFor1Day(mdService, figi, endDate);
            candles.addAll(collectedCandles);
            endDate = endDate.minus(1, ChronoUnit.DAYS);
        }
        log.info("start date for candles {}", OffsetDateTime.ofInstant(endDate, ZoneId.of("UTC")));
        log.info("collected {} candles", candles.size());
        rsiCacheService.calculateRSI(figi, cache);
        subscribeCandles(config);
    }


    private void subscribeCandles(RSIStrategyConfig config) {
        var figi = config.getFigi();
        var candles = cache.get(figi);

        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        StreamProcessor<MarketDataResponse> processor = response -> {
            if (response.hasCandle()) {
                log.info("new candles data for figi " + figi);

                var candle = CachedCandle.ofStreamCandle(response.getCandle());
                candles.add(candle);

                rsiCacheService.calculateRSI(figi, cache);
                signalHandler.handle(candle, config);
            } else if (response.hasSubscribeCandlesResponse()) {
                var successCount = response.getSubscribeCandlesResponse().getCandlesSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("success candles subscriptions: {}", successCount);
                log.info("error candles subscriptions: {}", errorCount);
            }
        };
        var marketDataStreamService = sdkService.getInvestApi().getMarketDataStreamService();

        //todo. Придумать способ, как открывать следующий стрим, если инструментов больше 300
        var streamName = "default stream id";
        var stream = marketDataStreamService.getStreamById(streamName);
        if (stream == null) {
            stream = marketDataStreamService.newStream(streamName, processor, onErrorCallback);
        }
        stream.subscribeCandles(List.of(figi), SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES);
    }
}
