package domain

/**
 * Результат аутентификации
 * Содержит код результата и сообщение с пояснением
 */
data class AuthResult(
    val resultCode: ResultCode,
    val message: String
)