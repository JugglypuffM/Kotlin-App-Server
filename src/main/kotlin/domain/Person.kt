package domain

/**
 * Класс с информацией о пользователе
 * Позже расширить, добавить: Возраст, дату рождения, прйдённое растояние и т.п.
 */
data class Person(
    val name: String,
    val login: String,
    val password: String
){
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(login.isNotBlank()) { "Login cannot be blank" }
        require(password.length >= 6) { "Password must be at least 6 characters long" }
    }

    override fun toString(): String {
        return "Person(name='$name', login='$login')"
    }
}
