package ru.tinkoff.rsistrategy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSIStrategyConfig {
    private String figi;
    //Количество дней, за которые собираем 15-минутные свечи
    private int initialCandlesSizeDays = 100;
    private BigDecimal upperRsiThreshold = BigDecimal.valueOf(70);
    private BigDecimal lowerRsiThreshold = BigDecimal.valueOf(30);
    private BigDecimal takeProfit = BigDecimal.valueOf(0.15);
    private BigDecimal stopLoss = BigDecimal.valueOf(0.05);
}