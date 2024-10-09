package database

import domain.Person
import java.util.Optional

class DatabaseMock : Database<Person> {
    private val data = mutableMapOf<String, Person>()

    override fun get(id: String): Optional<Person> {
        return Optional.ofNullable(data[id])
    }

    override fun add(id: String, entry: Person) {
        data[id] = entry
    }

    override fun update(id: String, entry: Person): Boolean {
        return if (data.containsKey(id)) {
            data[id] = entry
            true
        } else {
            false
        }
    }

    override fun delete(id: String): Boolean {
        return data.remove(id) != null
    }
}