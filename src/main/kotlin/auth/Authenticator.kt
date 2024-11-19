package auth

import database.dao.DAO.DatabaseException
import database.manager.DatabaseManager
import domain.Account
import domain.AuthResult
import domain.ResultCode

class Authenticator(private val databaseManager: DatabaseManager) {

    fun register(account: Account): AuthResult {
        try {
            databaseManager.addAccount(account)
            return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully registered.")
        }catch (e: DatabaseException) {
            return AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")
        }
    }

    fun login(login: String, password: String): AuthResult =
        databaseManager.getAccount(login).map<AuthResult> {
            if (it.password == password) {
                AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully logged in.")
            } else null
        }.orElse(
            AuthResult(ResultCode.INVALID_CREDENTIALS, "Wrong login or password.")
        )

}