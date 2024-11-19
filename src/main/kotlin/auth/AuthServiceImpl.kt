package auth

import domain.AuthResult
import domain.Account
import domain.ResultCode
import org.example.grpc.AuthProto.*
import org.example.grpc.AuthServiceGrpc
import io.grpc.stub.*

class AuthServiceImpl(private val authenticator: Authenticator) : AuthServiceGrpc.AuthServiceImplBase() {
    private fun createAuthResponse(authResult: AuthResult): AuthResponse {
        return AuthResponse.newBuilder()
            .setResultCode(authResult.resultCode.code)
            .setMessage(authResult.message)
            .build()
    }

    override fun register(request: RegisterRequest, responseObserver: StreamObserver<AuthResponse>) {
        try {
            val account = Account(request.login, request.password)
            val authResult = authenticator.register(account)

            responseObserver.onNext(createAuthResponse(authResult))
        }
        catch (_: IllegalArgumentException) {
            responseObserver.onNext(createAuthResponse(AuthResult(ResultCode.INVALID_CREDENTIALS, "Invalid login or password.")))
        }
        finally {
            responseObserver.onCompleted()
        }
    }

    override fun login(request: LoginRequest, responseObserver: StreamObserver<AuthResponse>) {
        val authResult = authenticator.login(request.login, request.password)

        responseObserver.onNext(createAuthResponse(authResult))
        responseObserver.onCompleted()
    }
}
