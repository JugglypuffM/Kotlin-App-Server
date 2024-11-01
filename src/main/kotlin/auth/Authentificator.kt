package auth

import database.Database
import domain.AuthResult
import domain.Account

class Authenticator(private val database: Database<Account>) {

    fun register(account: Account): AuthResult {
        if (database.get(account.login).isPresent) {
            return AuthResult(false, "User already exists.")
        }
        database.add(account)
        return AuthResult(true, "User successfully registered.")
    }

    fun login(login: String, password: String): AuthResult {
        val personOpt = database.get(login)
        if (personOpt.isPresent) {
            val person = personOpt.get()
            if (person.password == password) {
                return AuthResult(true, "User successfully logged in.")
            }
        }
        return AuthResult(false, "Invalid login or password.")
    }
}