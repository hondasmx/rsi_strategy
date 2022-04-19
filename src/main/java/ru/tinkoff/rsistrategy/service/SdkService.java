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
    private InvestApi sandboxApi;

    @Value("${app.config.token}")
    private String token;

    public InvestApi getInvestApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        if (investApi == null) {
            investApi = InvestApi.create(token);
        }
        return investApi;
    }

    public InvestApi getSandboxApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        if (sandboxApi == null) {
            sandboxApi = InvestApi.createSandbox(token);
        }
        return sandboxApi;
    }
}
