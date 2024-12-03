package database.dao

import database.tables.UsersTable
import domain.UserInfo
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*
import kotlin.jvm.optionals.getOrDefault

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
        return transaction {
            UsersTable.selectAll().where { UsersTable.login.eq(login) }
                .map { entry ->
                    UserInfo(
                        name = Optional.ofNullable(entry[UsersTable.name].takeIf { it.isNotBlank() }),
                        age = Optional.ofNullable(entry[UsersTable.age].takeIf { it > 0 }),
                        weight = Optional.ofNullable(entry[UsersTable.weight].takeIf { it > 0 }),
                        distance = Optional.ofNullable(entry[UsersTable.distance].takeIf { it > 0 })
                    )
                }
                .firstOrNull()
        }.let { Optional.ofNullable(it) }
    }


    override fun update(login: String, entry: UserInfo) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[name] = entry.name.getOrDefault("")
                    it[age] = entry.age.getOrDefault(0)
                    it[weight] = entry.weight.getOrDefault(0)
                    it[distance] = entry.distance.getOrDefault(0)
                }
            } catch (e: Exception) {
                throw DAO.DatabaseException("Information not exist for user with login: $login", e)
            }
        }
    }

    override fun add(entry: UserInfo) {}
    override fun delete(login: String) {}
}