package database

import domain.Account
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseMockTest {
    private lateinit var database: Database<Account>

    @BeforeEach
    fun setUp() {
        database = DatabaseMock()
    }

    @Test
    fun testAddAndGetPerson() {
        val account = Account(login = "johndoe", password = "password123")

        // Добавляем пользователя
        database.add(account)

        // Проверяем, что пользователь был добавлен
        val retrievedPerson = database.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(account, retrievedPerson.get())
    }

    @Test
    fun testGetNonExistentPerson() {
        val retrievedPerson = database.get("nonExistentId")
        assertFalse(retrievedPerson.isPresent)
    }

    @Test
    fun testUpdatePerson() {
        val account = Account(login = "janedoe", password = "password123")

        // Добавляем пользователя
        database.add(account)

        // Обновляем пользователя
        val updatedAccount = Account(login = "janedoe", password = "newpassword")
        database.update(account.login, updatedAccount)

        // Проверяем, что пользователь был обновлен
        val retrievedPerson = database.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(updatedAccount, retrievedPerson.get())
    }

    @Test
    fun testDeletePerson() {
        val account = Account(login = "marktwain", password = "password123")

        // Добавляем пользователя
        database.add(account)

        // Удаляем пользователя
        database.delete(account.login)

        // Проверяем, что пользователь был удален
        val retrievedPerson = database.get(account.login)
        assertFalse(retrievedPerson.isPresent)
    }
}
