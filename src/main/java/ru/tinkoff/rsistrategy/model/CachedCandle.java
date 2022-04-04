package ru.tinkoff.rsistrategy.model;

import com.google.protobuf.Timestamp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;

@EqualsAndHashCode(of = {"timestamp"})
@Getter
public class CachedCandle {
    private final BigDecimal closePrice;
    private final Timestamp timestamp;

    public CachedCandle(Quotation closePrice, Timestamp timestamp) {
        this.closePrice = MapperUtils.quotationToBigDecimal(closePrice);
        this.timestamp = timestamp;
    }

    public static CachedCandle ofHistoricCandle(HistoricCandle candle) {
        return new CachedCandle(candle.getClose(), candle.getTime());
    }

    public static CachedCandle ofStreamCandle(Candle candle) {
        return new CachedCandle(candle.getClose(), candle.getTime());
    }
}
