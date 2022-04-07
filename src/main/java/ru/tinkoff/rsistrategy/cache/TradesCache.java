package ru.tinkoff.rsistrategy.cache;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TradesCache {

    @Getter
    private final List<Trade> cache = new ArrayList<>();

    public void add(Trade trade) {
        cache.add(trade);
    }

    @Data
    public static class Trade {
        private final String figi;
        private final String reason;
        private final BigDecimal price;
        private final Direction direction;
    }

    public enum Direction {
        BUY,
        SELL
    }
}
