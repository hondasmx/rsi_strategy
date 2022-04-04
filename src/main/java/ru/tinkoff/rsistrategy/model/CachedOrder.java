package ru.tinkoff.rsistrategy.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class CachedOrder {
    private boolean shortOpen = false;
    private boolean longOpen = false;
    private BigDecimal openPrice = BigDecimal.TEN;
}
