package services.auth

import database.dao.DAO.DatabaseException
import database.manager.DatabaseManager
import domain.user.Account
import domain.auth.AuthResult
import domain.auth.ResultCode

class Authenticator(private val databaseManager: DatabaseManager): AuthenticatorInterface {

    override fun register(account: Account): AuthResult {
        try {
            databaseManager.addAccount(account)
            return AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully registered.")
        }catch (_: DatabaseException) {
            return AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")
        }
    }

    override fun login(login: String, password: String): AuthResult =
        databaseManager.getAccount(login).map<AuthResult> {
            if (it.password == password) {
                AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully logged in.")
            } else null
        }.orElse(
            AuthResult(ResultCode.INVALID_CREDENTIALS, "Wrong login or password.")
        )

}