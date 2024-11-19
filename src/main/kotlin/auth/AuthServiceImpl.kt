package auth

import domain.AuthResult
import domain.User
import domain.ResultCode
import grpc.AuthProto.AuthResponse
import grpc.AuthProto.LoginRequest
import grpc.AuthProto.RegisterRequest
import grpc.AuthServiceGrpc
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
            val user = User(request.name, request.login, request.password)
            val authResult = authenticator.register(user)

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
