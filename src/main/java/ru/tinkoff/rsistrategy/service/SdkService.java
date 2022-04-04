package ru.tinkoff.rsistrategy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Service
@Slf4j
@RequiredArgsConstructor
public class SdkService {
    private InvestApi investApi;

    @Value("${app.config.token}")
    private String token;

    public InvestApi getInvestApi() {

        if (investApi == null) {
            investApi = InvestApi.create(token);
        }
        return investApi;
    }
}
