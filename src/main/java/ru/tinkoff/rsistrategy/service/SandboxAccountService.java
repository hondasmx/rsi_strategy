package ru.tinkoff.rsistrategy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.PortfolioResponse;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SandboxAccountService {

    private final SdkService sdkService;
    @Value("${app.config.sandbox-account}")
    private String accountId;

    public String getAccountId() {
        if (!StringUtils.hasLength(accountId)) {
            log.info("no sandbox account was set. creating a new one");
            var sandboxService = sdkService.getInvestApi().getSandboxService();
            accountId = sandboxService.openAccountSync();
            log.info("new sandbox account: {}", accountId);
        }
        return accountId;
    }

    public PortfolioResponse getPortfolio() {
        return sdkService.getInvestApi().getSandboxService().getPortfolioSync(getAccountId());
    }

    public BigDecimal totalAmountOfFunds() {
        var portfolio = getPortfolio();
        var currencies = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountCurrencies());
        var etfs = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountEtf());
        var bonds = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountBonds());
        var futures = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountFutures());
        var shares = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountShares());
        var total = currencies.add(etfs).add(bonds).add(futures).add(shares);
        log.info("total: {}", total);
        return total;
    }

    private void addFunds(SandboxService sandboxService) {
        var amount = 1000000;
        var accountId = getAccountId();
        log.info("add funds for sandbox account: {}. amount: {}", accountId, amount);
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("rub").setUnits(amount).build());
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("usd").setUnits(amount).build());
        log.info("added funds for sandbox account: {}. amount: {}", accountId, amount);
    }
}
