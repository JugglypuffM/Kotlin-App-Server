package services.auth

import domain.auth.AuthResult
import domain.user.Account

interface AuthenticatorInterface {
    fun register(account: Account): AuthResult

    fun login(login: String, password: String): AuthResult
}