package database

import Exception.DatabaseException
import domain.User
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Users : Table() {
    val id : Column<String> = varchar("id", 10)
    val login : Column<String> = varchar("u_login", 45)
    val password : Column<String> = varchar("u_password", 35)
    val name : Column<String> = varchar("u_name", 20)
    override val primaryKey: PrimaryKey = PrimaryKey(id, name="PK_id")
}

class DatabaseUsers : Database<User>{

    init {
        //Проверка существования соединения с бд
        if (!TransactionManager.isInitialized()) {
            val dotenv = dotenv()
            org.jetbrains.exposed.sql.Database.connect(
                url = "jdbc:postgresql://localhost:${dotenv["PORT"]}/${dotenv["NAMEDB"]}",
                driver = "org.postgresql.Driver",
                user = dotenv["USER"].toString(),
                password = dotenv["PASSWORD"].toString()
            )
        }

        transaction {
            try {
                SchemaUtils.create(Users)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    override fun get(id: String): Optional<User> {
        var user: User? = null
        transaction {
            try {
                for (entry in Users.selectAll().where { Users.id eq id }) {
                    user = User(entry[Users.name], entry[Users.login], entry[Users.password])
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(user)
    }

    override fun delete(id: String) {
        transaction {
            try {
                Users.deleteWhere { this.id eq id }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun update(id: String, entry: User) {
        transaction {
            try {
                Users.update({ Users.id eq id }) {
                    it[login] = entry.login
                    it[password] = entry.password
                    it[name] = entry.name
                }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun add(id: String, entry: User) {
        transaction {
            try {
                Users.insert {
                    it[this.id] = id
                    it[login] = entry.login
                    it[password] = entry.password
                    it[name] = entry.name
                }
            } catch (e: Exception) {
                throw DatabaseException("Error adding user with id: $id", e)
            }
        }
    }
}