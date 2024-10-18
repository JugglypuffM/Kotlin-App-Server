package database

import domain.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseMockTest {
    private lateinit var database: Database<User>

    @BeforeEach
    fun setUp() {
        database = DatabaseMock()
    }

    @Test
    fun testAddAndGetPerson() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")
        val id = "1"

        // Добавляем пользователя
        database.add(id, user)

        // Проверяем, что пользователь был добавлен
        val retrievedPerson = database.get(id)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(user, retrievedPerson.get())
    }

    @Test
    fun testGetNonExistentPerson() {
        val retrievedPerson = database.get("nonExistentId")
        assertFalse(retrievedPerson.isPresent)
    }

    @Test
    fun testUpdatePerson() {
        val user = User(name = "Jane Doe", login = "janedoe", password = "password123")
        val id = "2"

        // Добавляем пользователя
        database.add(id, user)

        // Обновляем пользователя
        val updatedUser = User(name = "Jane Smith", login = "janesmith", password = "newpassword")
        val updateResult = database.update(id, updatedUser)

        assertTrue(updateResult)

        // Проверяем, что пользователь был обновлен
        val retrievedPerson = database.get(id)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(updatedUser, retrievedPerson.get())
    }

    @Test
    fun testDeletePerson() {
        val user = User(name = "Mark Twain", login = "marktwain", password = "password123")
        val id = "3"

        // Добавляем пользователя
        database.add(id, user)

        // Удаляем пользователя
        val deleteResult = database.delete(id)
        assertTrue(deleteResult)

        // Проверяем, что пользователь был удален
        val retrievedPerson = database.get(id)
        assertFalse(retrievedPerson.isPresent)
    }
}
