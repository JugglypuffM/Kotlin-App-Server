package database

import domain.Account
import domain.User
import io.github.cdimascio.dotenv.dotenv
import java.util.Optional

/**
 * Менеджер для выполнения запросов к базе данных
 */
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
        dbTableUsers = DbTableUsersInformation()
    }

    /**
     * Метод для внесения нового пользователя в базу данных
     */
    fun addAccount(account: Account) {
        dbTableAccounts.add(account)
    }

    /**
     * Метод для удаления всех данных о пользователе
     */
    fun deleteAccount(login: String) {
        dbTableAccounts.delete(login)
    }

    /**
     * Метод для обновления учётной записи пользователя
     */
    fun updateAccount(login: String, account: Account) {
        dbTableAccounts.update(login, account)
    }

    /**
     * Метод для получения учётной записи пользователя
     */
    fun getAccount(login: String) : Optional<Account> {
        return dbTableAccounts.get(login)
    }

    /**
     * Метод для обновления информации о пользователе
     */
    fun updateUserInformation(login: String, user: User) {
        dbTableUsers.update(login, user)
    }

    /**
     * Метод для получения дополнительной информации об аккаунте пользователя
     */
    fun getUserInformation(login: String) : Optional<User> {
        return dbTableUsers.get(login)
    }
}