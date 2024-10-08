import java.util.*

interface Database<T> {
    fun get(id: String): Optional<T>
    fun add(id: String, person: T)
    fun update(id: String, person: T): Boolean
    fun delete(id: String): Boolean
}