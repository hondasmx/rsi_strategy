package ru.tinkoff.rsistrategy.signal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.rsistrategy.cache.OrdersCache;
import ru.tinkoff.rsistrategy.cache.RSICache;
import ru.tinkoff.rsistrategy.model.CachedCandle;
import ru.tinkoff.rsistrategy.model.RSIStrategyConfig;
import ru.tinkoff.rsistrategy.service.OrderService;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class RSISignalHandler {

    private final RSICache rsiCache;
    private final OrderService orderService;
    private final OrdersCache ordersCache;

    public void handle(CachedCandle candle, RSIStrategyConfig config) {
        var figi = config.getFigi();
        var closePrice = candle.getClosePrice();

        var rsi = rsiCache.getCache().get(figi).get(100);

        checkForOpenShort(rsi, figi, closePrice, config);
        checkForOpenLong(rsi, figi, closePrice, config);
        checkForCloseShort(figi, closePrice, config);
        checkForCloseLong(figi, closePrice, config);
    }

    private void checkForOpenLong(BigDecimal rsi, String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var lower = config.getLowerRsiThreshold();
        if (rsi.compareTo(lower) >= 0) {
            return;
        }
        var longOpen = ordersCache.longOpen(figi);
        //Если уже куплен в лонг - выходим
        if (longOpen) {
            return;
        }
        log.info("opening long. figi: {}, close price: {}, RSI: {}", figi, closePrice, rsi);
        orderService.buyMarketLong(figi);
    }

    private void checkForOpenShort(BigDecimal rsi, String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var upper = config.getUpperRsiThreshold();
        if (rsi.compareTo(upper) <= 0) {
            return;
        }
        var shortOpen = ordersCache.shortOpen(figi);
        //Если уже куплен в шорт - выходим
        if (shortOpen) {
            return;
        }
        log.info("opening short. figi: {}, close price: {}, RSI: {}", figi, closePrice, rsi);
        orderService.buyMarketShort(figi);
    }

    private void checkForCloseShort(String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var shortOpen = ordersCache.shortOpen(figi);
        //Если уже продан в шорт - выходим
        if (!shortOpen) {
            return;
        }

        var takeProfit = BigDecimal.ONE.subtract(config.getTakeProfit());
        var stopLoss = config.getStopLoss().add(BigDecimal.ONE);

        var openPrice = ordersCache.getPrice(figi);
        var longOpen = ordersCache.longOpen(figi);
        String reason = null;
        if (longOpen) {
            reason = "long opened";
        } else if (openPrice.multiply(takeProfit).compareTo(closePrice) >= 0) {
            reason = "take profit";
        } else if (openPrice.multiply(stopLoss).compareTo(closePrice) <= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            log.info("closing short. figi: {}, open price: {}, close price: {}, reason: {}", figi, openPrice, closePrice, reason);
            orderService.sellMarketShort(figi);
        }
    }


    private void checkForCloseLong(String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var longOpen = ordersCache.longOpen(figi);

        //Если уже продан в лонг, либо ничего нет - выходим
        if (!longOpen) {
            return;
        }

        var takeProfit = config.getTakeProfit().add(BigDecimal.ONE);
        var stopLoss = BigDecimal.ONE.subtract(config.getStopLoss());

        var openPrice = ordersCache.getPrice(figi);
        var shortOpen = ordersCache.shortOpen(figi);
        String reason = null;
        if (shortOpen) {
            reason = "short opened";
        }
        else if (openPrice.multiply(takeProfit).compareTo(closePrice) >= 0) {
            reason = "take profit";
        }

        else if (openPrice.multiply(stopLoss).compareTo(closePrice) <= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            log.info("closing long. figi: {}, open price: {}, close price: {}, reason: {}", figi, openPrice, closePrice, reason);
            orderService.sellMarketLong(figi);
        }
    }
}
