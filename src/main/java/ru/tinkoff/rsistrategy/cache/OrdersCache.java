package ru.tinkoff.rsistrategy.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.utils.MapperUtils;
import ru.tinkoff.rsistrategy.model.CachedOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrdersCache {

    private final Map<String, CachedOrder> cache = new HashMap<>();

    //цена * лот
    public void setPrice(String figi, MoneyValue openPrice) {
        getCachedOrder(figi).setOpenPrice(MapperUtils.moneyValueToBigDecimal(openPrice));
    }

    //цена * лот
    public BigDecimal getPrice(String figi) {
        return cache.get(figi).getOpenPrice();
    }

    public boolean shortOpen(String figi) {
        return getCachedOrder(figi).isShortOpen();
    }

    public boolean longOpen(String figi) {
        return getCachedOrder(figi).isLongOpen();
    }

    public void setLong(String figi, boolean value) {
        getCachedOrder(figi).setLongOpen(value);
    }

    public void setShort(String figi, boolean value) {
        getCachedOrder(figi).setShortOpen(value);
    }

    private CachedOrder getCachedOrder(String figi) {
        var cachedOrder = cache.get(figi);
        if (cachedOrder == null) {
            cachedOrder = new CachedOrder();
            cache.put(figi, cachedOrder);
        }
        return cachedOrder;
    }
}
