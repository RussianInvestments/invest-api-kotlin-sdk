# Публикация новой версии
## Цифровая подпись
Скрипты сборки сконфигурированы, чтобы артефакты были подписаны цифровой подписью с помощью плагина `signing` и утилиты `gpg`  
Для настройки подписи необходимо создать/изменить файл `gradle.properties` (глобальные настройки) в директории `~/.gradle`:  
```properties
signing.keyId=<Идентификатор ключа>
signing.password=<Приватный ключ>
signing.secretKeyRingFile=<Полный путь к gpg-файлу>
```
keyId должен быть опубликован в [hkps://keys.openpgp.org](https://keys.openpgp.org/)  
## Публикация
Для публикации необходимо выполнить настройку авторизации для репозитория OSS.  
Для этого необходимо создать/изменить файл `gradle.properties` (глобальные настройки) в директории `~/.gradle`:  
```properties
ossh.username=<Имя пользователя>
ossh.password=<Пароль/токен>
```
После указания настроек авторизации можно выполнить публикацию.  