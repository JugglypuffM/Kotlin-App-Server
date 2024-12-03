package domain

import java.util.Optional

/**
 * Класс с информацией о пользователе
 */
data class UserInfo(
    val name: Optional<String>,
    val age: Optional<Int>,
    val weight: Optional<Int>,
    val distance: Optional<Int>,
)
