# Пример RSI стратегии

## Пререквизиты
- Java версии не ниже 11
- Gradle версии не ниже 5.0
- Подключенная маржинальная торговля для выставления коротких (short) позиций 

## Описание стратегии
RSI (relative strength index) - индекс относительной силы. Индикатор показывается насколько сильно актив перекуплен (т.е. стоит слишком дорого) или перепродан (т.е. стоит слишком дешево) относительно средних значений.
Если RSI высоко, то это сигнал к продаже актива, если низко - к покупке. 

Классическими являются отсечки 30 и 70 (значение индикатора выше 70 - продавать, ниже 30 - покупать). Однако, у всех активов разные характеристики, 
поэтому лучшим решением будет определение этого диапазона для каждого актива индивидуально.

В данной реализации при выставлении short/long так же идет слежение за последней ценой.

При торговле в long:
- Если цена выросла на 15% (значение в конфиге takeProfit) - выставляется рыночная заявка на продажу для фиксирования прибыли
- Если цена упала на 5% (значение в конфиге stopLoss) - выставляется рыночная заявка на продажу для фиксирования убытка

При торговле в short:
- Если цена упала на 15% (значение в конфиге takeProfit) - выставляется рыночная заявка на покупку для фиксирования прибыли
- Если цена выросла на 5% (значение в конфиге stopLoss) - выставляется рыночная заявка на покупку для фиксирования убытка

## Запуск и конфигурация
После запуска приложения будут доступны 2 эндпоинта
- GET http://localhost:8081/rsi - получение актуальных подписок
- POST http://localhost:8081/rsi - добавление в пул подписок. Пример конфига см ниже
- GET http://localhost:8081/portfolio - общая стоимость портфеля

## Пример конфига
```json 
{
"figi": "BBG004730ZJ9",
"upperRsiThreshold": 70,
"lowerRsiThreshold": 30,
"takeProfit": 0.15,
"stopLoss": 0.05,
"rsiPeriod": 14
}
```
- Figi. Идентификатор инструмента
- upperRsiThreshold. Не обязательный параметр. Значение по-умолчанию 70. Верхнее значение RSI, после которого будет дан сигнал на покупку в short
- lowerRsiThreshold. Не обязательный параметр. Значение по-умолчанию 30. Нижнее значение RSI, после которого будет дан сигнал на покупку в long
- takeProfit. Не обязательный параметр. Значение по-умолчанию 0.15 (15%). Значение для take profit 
- stopLoss. Не обязательный параметр. Значение по-умолчанию 0.05 (5%). Значение для stop loss
- rsiPeriod. Не обязательный параметр. Значение по-умолчанию 14. Количество последних свечей, по которым будет рассчитан RSI. 