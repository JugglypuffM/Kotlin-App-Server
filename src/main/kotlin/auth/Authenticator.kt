package auth

import database.DatabaseTable
import domain.AuthResult
import domain.Account

class Authenticator(private val databaseTable: DatabaseTable<Account>) {

    fun register(account: Account): AuthResult {
        if (databaseTable.get(account.login).isPresent) {
            return AuthResult(false, "User already exists.")
        }
        databaseTable.add(account)
        return AuthResult(true, "User successfully registered.")
    }

    fun login(login: String, password: String): AuthResult {
        val personOpt = databaseTable.get(login)
        if (personOpt.isPresent) {
            val person = personOpt.get()
            if (person.password == password) {
                return AuthResult(true, "User successfully logged in.")
            }
        }
        return AuthResult(false, "Invalid login or password.")
    }
}