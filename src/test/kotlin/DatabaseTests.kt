import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class InMemoryDatabase : Database<Person> {
    private val users = mutableMapOf<String, Person>()

    override fun get(id: String): Optional<Person> {
        return Optional.ofNullable(users[id])
    }

    override fun add(id: String, person: Person) {
        users[id] = person
    }

    override fun update(id: String, person: Person): Boolean {
        return if (users.containsKey(id)) {
            users[id] = person
            true
        } else {
            false
        }
    }

    override fun delete(id: String): Boolean {
        return users.remove(id) != null
    }
}


class PersonDatabaseTest {

    private lateinit var database: Database<Person>

    @BeforeEach
    fun setUp() {
        database = InMemoryDatabase()
    }

    @Test
    fun testAddAndGetPerson() {
        val person = Person(name = "John Doe", login = "johndoe", password = "password123")
        val id = "1"

        // Добавляем пользователя
        database.add(id, person)

        // Проверяем, что пользователь был добавлен
        val retrievedPerson = database.get(id)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(person, retrievedPerson.get())
    }

    @Test
    fun testGetNonExistentPerson() {
        val retrievedPerson = database.get("nonExistentId")
        assertFalse(retrievedPerson.isPresent)
    }

    @Test
    fun testUpdatePerson() {
        val person = Person(name = "Jane Doe", login = "janedoe", password = "password123")
        val id = "2"

        // Добавляем пользователя
        database.add(id, person)

        // Обновляем пользователя
        val updatedPerson = Person(name = "Jane Smith", login = "janesmith", password = "newpassword")
        val updateResult = database.update(id, updatedPerson)

        assertTrue(updateResult)

        // Проверяем, что пользователь был обновлен
        val retrievedPerson = database.get(id)
        assertTrue(retrievedPerson.isPresent)
        assertEquals(updatedPerson, retrievedPerson.get())
    }

    @Test
    fun testDeletePerson() {
        val person = Person(name = "Mark Twain", login = "marktwain", password = "password123")
        val id = "3"

        // Добавляем пользователя
        database.add(id, person)

        // Удаляем пользователя
        val deleteResult = database.delete(id)
        assertTrue(deleteResult)

        // Проверяем, что пользователь был удален
        val retrievedPerson = database.get(id)
        assertFalse(retrievedPerson.isPresent)
    }
}
