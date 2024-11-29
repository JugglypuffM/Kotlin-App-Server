package database.manager

import domain.user.Account
import domain.user.UserInfo
import java.util.Optional

/**
 * Интерфейс менеджер для выполнения запросов к базе данных
 */
interface DatabaseManager {
    /**
     * Метод для внесения нового пользователя в базу данных
     */
    fun addAccount(account: Account)

    /**
     * Метод для удаления всех данных о пользователе
     */
    fun deleteAccount(login: String)

    /**
     * Метод для обновления учётной записи пользователя
     */
    fun updateAccount(login: String, account: Account)

    /**
     * Метод для получения учётной записи пользователя
     */
    fun getAccount(login: String) : Optional<Account>

    /**
     * Метод для обновления информации о пользователе
     */
    fun updateUserInformation(login: String, userInfo: UserInfo)

    /**
     * Метод для получения дополнительной информации об аккаунте пользователя
     */
    fun getUserInformation(login: String) : Optional<UserInfo>
}