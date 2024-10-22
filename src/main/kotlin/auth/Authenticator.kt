package auth

import database.Database
import domain.AuthResult
import domain.Person
import domain.ResultCode

class Authenticator(private val database: Database<Person>) {

    fun register(person: Person): AuthResult {
        if (database.get(person.login).isPresent) {
            return AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")
        }
        database.add(person.login, person)
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