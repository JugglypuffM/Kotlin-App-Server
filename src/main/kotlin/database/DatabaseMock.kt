package database

import domain.User
import java.util.Optional

class DatabaseMock : Database<User> {
    private val data = mutableMapOf<String, User>()

    override fun get(id: String): Optional<User> {
        return Optional.ofNullable(data[id])
    }

    override fun add(id: String, entry: User) {
        data[id] = entry
    }

    override fun update(id: String, entry: User): Boolean {
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