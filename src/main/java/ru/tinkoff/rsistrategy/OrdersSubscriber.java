package ru.tinkoff.rsistrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.TradesStreamResponse;
import ru.tinkoff.piapi.core.stream.StreamProcessor;
import ru.tinkoff.rsistrategy.cache.TradesCache;
import ru.tinkoff.rsistrategy.service.SdkService;

import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersSubscriber {

    private final SdkService sdkService;
    private final TradesCache tradesCache;

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeTrades() {
        StreamProcessor<TradesStreamResponse> consumer = response -> {
            if (response.hasOrderTrades()) {
                log.info("Новые данные по сделкам: {}", response);
//                tradesCache.add(new TradesCache.Trade(response));
            }
        };

        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        sdkService.getInvestApi().getOrdersStreamService().subscribeTrades(consumer, onErrorCallback);
    }
}
