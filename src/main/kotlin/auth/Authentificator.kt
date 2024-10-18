package auth

import database.Database
import domain.AuthResult
import domain.User

class Authenticator(private val database: Database<User>) {

    fun register(user: User): AuthResult {
        if (database.get(user.login).isPresent) {
            return AuthResult(false, "User already exists.")
        }
        database.add(user.login, user)
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