package auth

import database.DatabaseTable
import domain.AuthResult
import domain.Account
import domain.ResultCode

class Authenticator(private val databaseTable: DatabaseTable<Account>) {

    fun register(account: Account): AuthResult {
        if (databaseTable.get(account.login).isPresent) {
            return AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")
        }
        databaseTable.add(account)
        return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully registered.")
    }

    fun login(login: String, password: String): AuthResult {
        val personOpt = databaseTable.get(login)
        if (personOpt.isPresent) {
            val person = personOpt.get()
            if (person.password == password) {
                return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully logged in.")
            }
        }
        return AuthResult(ResultCode.INVALID_CREDENTIALS, "Wrong login or password.")
    }
}