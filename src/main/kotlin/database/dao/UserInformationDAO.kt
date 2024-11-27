package database.dao

import database.tables.UsersTable
import domain.UserInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserInformationDAO : DAO<UserInfo> {
    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    override fun get(login: String): Optional<UserInfo> {
        var userInfo: UserInfo? = null
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    userInfo = UserInfo(
                        name = entry[UsersTable.name],
                        age = entry[UsersTable.age],
                        weight = entry[UsersTable.weight],
                        distance = entry[UsersTable.distance])
                }
            } catch (e: Exception){
                throw DAO.DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(userInfo)
    }

    override fun update(login: String, entry: UserInfo) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[name] = entry.name
                    it[age] = entry.age
                    it[weight] = entry.weight
                    it[distance] = entry.distance
                }
            } catch (e: Exception) {
                throw DAO.DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun add(entry: UserInfo) {}
    override fun delete(login: String) {}
}