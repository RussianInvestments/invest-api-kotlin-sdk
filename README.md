[![License](https://img.shields.io/github/license/RussianInvestments/invest-api-java-sdk?style=flat-square&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)

# Kotlin SDK для Tinkoff Invest API

Данный проект представляет собой инструментарий на языке Kotlin для работы с API Тинькофф Инвестиции, который можно
использовать для создания торговых роботов.

## Пререквизиты
- Java версии не ниже 8
- Kotlin версии не ниже 1.9
- Maven версии не ниже 3, либо Gradle версии не ниже 5.0


## Использование

Для начала работы подключите к вашему проекту core-модуль

|     Система сборки     | Код                                                                                                                                                                                                                                                                                                                                                 |
|:----------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|         Maven          | <b>\<dependency></b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>\<groupId></b>ru.t-technologies.invest.piapi.kotlin<b>\</groupId></b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>\<artifactId></b>kotlin-sdk-grpc-core<b>\</artifactId></b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>\<version></b>1.35.0<b>\</version></b><br><b>\</dependency></b> |
| Gradle with Groovy DSL | <b>implementation</b> 'ru.t-technologies.invest.piapi.kotlin:kotlin-sdk-grpc-core:1.35.0'                                                                                                                                                                                                                                                           |
| Gradle with Kotlin DSL | <b>implementation</b>("ru.t-technologies.invest.piapi.kotlin:kotlin-sdk-grpc-core:1.35.0")                                                                                                                                                                                                                                                          |



После этого можно пользоваться инструментарием

```kotlin
import ru.ttech.piapi.core.InvestApi

var token = "<secret-token>"
var api = InvestApi.createApi(InvestApi.defaultChannel(token = token, target = "invest-public-api.tinkoff.ru:443"))

var order = api.ordersService.postOrderSync(...)
```

## Сборка
### JVM
Для сборки перейдите в директорию проекта и выполните команду сборки gradle
```
gradle clean build
```

## Предложения и пожелания к SDK

Смело выносите свои предложения в Issues, задавайте вопросы. Pull Request'ы также принимаются.

## У меня есть вопрос по работе API

Документация к API находится в [отдельном репозитории](https://github.com/RussianInvestments/investAPI). Там вы можете задать
вопрос в Issues.