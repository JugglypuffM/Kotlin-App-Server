package database

import database.dao.DAO
import database.dao.DbTableMock
import domain.Account
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DbTableMockTest {
    private lateinit var AccountDAO: DAO<Account>

    @BeforeEach
    fun setUp() {
        AccountDAO = DbTableMock()
    }

    @Test
    fun testAddAndGetPerson() {
        val account = Account(login = "johndoe", password = "password123")

        // Добавляем пользователя
        AccountDAO.add(account)

        // Проверяем, что пользователь был добавлен
        val retrievedPerson = AccountDAO.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(account, retrievedPerson.get())
    }

    @Test
    fun testGetNonExistentPerson() {
        val retrievedPerson = AccountDAO.get("nonExistentId")
        assertFalse(retrievedPerson.isPresent)
    }

    @Test
    fun testUpdatePerson() {
        val account = Account(login = "janedoe", password = "password123")

        // Добавляем пользователя
        AccountDAO.add(account)

        // Обновляем пользователя
        val updatedAccount = Account(login = "janedoe", password = "newpassword")
        AccountDAO.update(account.login, updatedAccount)

        // Проверяем, что пользователь был обновлен
        val retrievedPerson = AccountDAO.get(account.login)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(updatedAccount, retrievedPerson.get())
    }

    @Test
    fun testDeletePerson() {
        val account = Account(login = "marktwain", password = "password123")

        // Добавляем пользователя
        AccountDAO.add(account)

        // Удаляем пользователя
        AccountDAO.delete(account.login)

        // Проверяем, что пользователь был удален
        val retrievedPerson = AccountDAO.get(account.login)
        assertFalse(retrievedPerson.isPresent)
    }
}
