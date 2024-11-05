package database

import domain.Account
import domain.User
import io.github.cdimascio.dotenv.dotenv
import java.util.Optional

object DatabaseManager {
    private val url = "jdbc:postgresql://localhost:${dotenv()["DB_PORT"]}/${dotenv()["DB_NAME"]}"
    private val dbUser = dotenv()["PSQL_USER"].toString()
    private val dbPassword = dotenv()["PSQL_PASS"].toString()
    private val dbTableAccounts : DatabaseTable<Account>
    private val dbTableUsers : DatabaseTable<User>
    init {
        org.jetbrains.exposed.sql.Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
        dbTableAccounts = DbTableAccounts()
        dbTableUsers = DbTableUsers()
    }

    fun addNewAccount(account: Account) {
        dbTableAccounts.add(account)
    }

    fun deleteAccount(login: String) {
        dbTableAccounts.delete(login)
    }

    fun updateAccount(login: String, account: Account) {
        dbTableAccounts.update(login, account)
    }

    fun getAccount(login: String) : Optional<Account> {
        return dbTableAccounts.get(login)
    }

    fun deleteUser(login: String) {
        dbTableUsers.delete(login)
    }

    fun updateUser(login: String, user: User) {
        dbTableUsers.update(login, user)
    }

    fun getUser(login: String) : Optional<User> {
        return dbTableUsers.get(login)
    }
}