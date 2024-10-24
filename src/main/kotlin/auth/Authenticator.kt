package auth

import database.Database
import domain.AuthResult
import domain.User
import domain.ResultCode

class Authenticator(private val database: Database<User>) {

    fun register(user: User): AuthResult {
        if (database.get(user.login).isPresent) {
            return AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")
        }
        database.add(user.login, user)
        return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully registered.")
    }

    fun login(login: String, password: String): AuthResult {
        val personOpt = database.get(login)
        if (personOpt.isPresent) {
            val person = personOpt.get()
            if (person.password == password) {
                return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully logged in.")
            }
        }
        return AuthResult(ResultCode.INVALID_CREDENTIALS, "Wrong login or password.")
    }
}