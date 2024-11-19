package database

import domain.Account
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DbTableMockTest {
    private lateinit var databaseTable: DatabaseTable<Account>

    @BeforeEach
    fun setUp() {
        databaseTable = DbTableMock()
    }

    @Test
    fun testAddAndGetPerson() {
        val account = Account(login = "johndoe", password = "password123")

        // Добавляем пользователя
        databaseTable.add(account)

        // Проверяем, что пользователь был добавлен
        val retrievedPerson = databaseTable.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(account, retrievedPerson.get())
    }

    @Test
    fun testGetNonExistentPerson() {
        val retrievedPerson = databaseTable.get("nonExistentId")
        assertFalse(retrievedPerson.isPresent)
    }

    @Test
    fun testUpdatePerson() {
        val account = Account(login = "janedoe", password = "password123")

        // Добавляем пользователя
        databaseTable.add(account)

        // Обновляем пользователя
        val updatedAccount = Account(login = "janedoe", password = "newpassword")
        databaseTable.update(account.login, updatedAccount)

        // Проверяем, что пользователь был обновлен
        val retrievedPerson = databaseTable.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(updatedAccount, retrievedPerson.get())
    }

    @Test
    fun testDeletePerson() {
        val account = Account(login = "marktwain", password = "password123")

        // Добавляем пользователя
        databaseTable.add(account)

        // Удаляем пользователя
        databaseTable.delete(account.login)

        // Проверяем, что пользователь был удален
        val retrievedPerson = databaseTable.get(account.login)
        assertFalse(retrievedPerson.isPresent)
    }
}
