package database


import database.tables.Users
import domain.Account
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*



class DatabaseAccounts : Database<Account>{

    init {
        //Проверка существования соединения с бд
        if (!TransactionManager.isInitialized()) {
            org.jetbrains.exposed.sql.Database.connect(
                url = "jdbc:postgresql://localhost:${dotenv()["DB_PORT"]}/${dotenv()["DB_NAME"]}",
                driver = "org.postgresql.Driver",
                user = dotenv()["PSQL_USER"].toString(),
                password = dotenv()["PSQL_PASS"].toString()
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

    override fun get(login: String): Optional<Account> {
        var account: Account? = null
        transaction {
            try {
                for (user in Users.selectAll().where { Users.login.eq(login) }) {
                    account = Account(user[Users.login], user[Users.password])
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(account)
    }

    override fun delete(login: String) {
        transaction {
            try {
                Users.deleteWhere { Users.login.eq(login) }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun update(login: String, entry: Account) {
        transaction {
            try {
                Users.update({ Users.login.eq(login)}) {
                    it[Users.login] = entry.login
                    it[Users.password] = entry.password
                }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    override fun add(entry: Account) {
        transaction {
            try {
                Users.insert {
                    it[Users.login] = entry.login
                    it[Users.password] = entry.password
                }
            } catch (e: Exception) {
                throw DatabaseException("Error adding user with id: $id", e)
            }
        }
    }
}