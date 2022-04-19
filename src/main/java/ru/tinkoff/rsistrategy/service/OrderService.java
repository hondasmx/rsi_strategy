package ru.tinkoff.rsistrategy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.rsistrategy.cache.OrdersCache;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final SdkService sdkService;
    private final OrdersCache ordersCache;
    private final SandboxAccountService sandboxAccountService;
    @Value("${app.config.number-of-lots}")
    private int numberOfLots;


    public void sellMarketLong(String figi) {
        var executedPrice = sellMarket(figi);
        ordersCache.setLong(figi, false);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void buyMarketLong(String figi) {
        var executedPrice = buyMarket(figi);
        ordersCache.setLong(figi, true);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void sellMarketShort(String figi) {
        var executedPrice = buyMarket(figi);
        ordersCache.setShort(figi, false);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void buyMarketShort(String figi) {
        var executedPrice = sellMarket(figi);
        ordersCache.setShort(figi, true);
        ordersCache.setPrice(figi, executedPrice);
    }

    private MoneyValue sellMarket(String figi) {
        var orderId = UUID.randomUUID().toString();
        var accountId = sandboxAccountService.getAccountId();
        return sdkService.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }

    private MoneyValue buyMarket(String figi) {
        var orderId = UUID.randomUUID().toString();
        var accountId = sandboxAccountService.getAccountId();
        return sdkService.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }
}
