package database

import database.tables.UsersTable
import domain.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DbTableUsersInformation : DatabaseTable<User> {
    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    override fun get(login: String): Optional<User> {
        var user: User? = null
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    user = User(
                        name = entry[UsersTable.name],
                        age = entry[UsersTable.age],
                        weight = entry[UsersTable.weight],
                        distance = entry[UsersTable.distance])
                }
            } catch (e: Exception){
                throw DatabaseTable.DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(user)
    }

    override fun update(login: String, entry: User) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[name] = entry.name
                    it[age] = entry.age
                    it[weight] = entry.weight
                    it[distance] = entry.distance
                }
            } catch (e: Exception) {
                throw DatabaseTable.DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun add(entry: User) {}
    override fun delete(login: String) {}
}