package domain

/**
 * Люгин и пароль пользователя
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
        return "domain.Account(login='$login', password='$password')"
    }
}
