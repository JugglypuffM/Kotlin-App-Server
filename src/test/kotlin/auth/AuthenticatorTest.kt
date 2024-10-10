package auth

import database.DatabaseMock
import domain.Person
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticatorTest {

    private lateinit var database: DatabaseMock
    private lateinit var authenticator: Authenticator

    @BeforeEach
    fun setUp() {
        database = DatabaseMock()
        authenticator = Authenticator(database)
    }

    @Test
    fun `should register user successfully`() {
        val person = Person(name = "John Doe", login = "johndoe", password = "password123")

        val result = authenticator.register(person)

        assertTrue(result.success)
        assertEquals("User successfully registered.", result.message)
        assertTrue(database.get("johndoe").isPresent)
        assertEquals(person, database.get("johndoe").get())
    }

    @Test
    fun `should not register user with existing login`() {
        val person1 = Person(name = "John Doe", login = "johndoe", password = "password123")
        val person2 = Person(name = "Jane Doe", login = "johndoe", password = "password456")

        authenticator.register(person1)
        val result = authenticator.register(person2)

        assertFalse(result.success)
        assertEquals("User already exists.", result.message)
        assertEquals(person1, database.get("johndoe").get())
    }

    @Test
    fun `should login user successfully`() {
        val person = Person(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(person)
        val result = authenticator.login("johndoe", "password123")

        assertTrue(result.success)
        assertEquals("User successfully logged in.", result.message)
    }

    @Test
    fun `should not login with incorrect password`() {
        val person = Person(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(person)
        val result = authenticator.login("johndoe", "wrongpassword")

        assertFalse(result.success)
        assertEquals("Invalid login or password.", result.message)
    }

    @Test
    fun `should not login non-existing user`() {
        val result = authenticator.login("nonexisting", "somepassword")

        assertFalse(result.success)
        assertEquals("Invalid login or password.", result.message)
    }
}
