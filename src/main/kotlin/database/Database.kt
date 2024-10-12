package database

import java.util.*

interface Database<T> {
    fun get(id: String): Optional<T>
    fun add(id: String, entry: T): Boolean
    fun update(id: String, entry: T): Boolean
    fun delete(id: String): Boolean
}