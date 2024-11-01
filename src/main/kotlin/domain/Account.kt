package domain

/**
 * Класс с информацией о пользователе
 * Позже расширить, добавить: Возраст, дату рождения, прйдённое растояние и т.п.
 */
data class Account(
    val login: String,
    val password: String,
){
    init {
        require(login.isNotBlank()) { "Login cannot be blank" }
        require(password.length >= 6) { "Password must be at least 6 characters long" }
    }

    override fun toString(): String {
        return "domain.Person(login='$login', password='$password')"
    }
}
