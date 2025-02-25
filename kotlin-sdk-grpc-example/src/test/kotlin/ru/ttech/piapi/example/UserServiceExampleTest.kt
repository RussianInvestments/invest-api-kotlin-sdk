package ru.ttech.piapi.example

import io.grpc.StatusException
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserServiceExampleTest {
    private val syncExample = UserServiceSyncExample()
    private val asyncExample = UserServiceAsyncExample()

    @Test
    @DisplayName("Обращение к user service c помощью асинхронного метода")
    fun testUserInfo() {
        Assertions.assertDoesNotThrow {
            // запрашиваем и выводим все аккаунты в консоль
            runBlocking {
                async {
                    asyncExample.exampleUserInfo()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Синхронное обращение к user service")
    fun testUserInfoSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleUserInfo()
        }
    }

    @Test
    @DisplayName("Получение ошибки unauthenticated")
    fun testUnauthenticated() {
        Assertions.assertThrows(StatusException::class.java) {
            syncExample.exampleUnauthenticatedAccess()
        }
    }

}