package auth

import domain.AuthResult
import domain.User
import org.example.grpc.AuthProto.*
import org.example.grpc.AuthServiceGrpc
import io.grpc.stub.*

class AuthServiceImpl(private val authenticator: Authenticator) : AuthServiceGrpc.AuthServiceImplBase() {
    private fun createAuthResponse(authResult: AuthResult): AuthResponse {
        return AuthResponse.newBuilder()
            .setSuccess(authResult.success)
            .setMessage(authResult.message)
            .build()
    }

    override fun register(request: RegisterRequest, responseObserver: StreamObserver<AuthResponse>) {
        try {
            val user = User(request.name, request.login, request.password)
            val authResult = authenticator.register(user)
            //TODO: запомнить факт логина где-нибудь в БД

            responseObserver.onNext(createAuthResponse(authResult))
        }
        catch (e: IllegalArgumentException) {
            responseObserver.onNext(createAuthResponse(AuthResult(false, "Invalid login or password.")))
        }
        finally {
            responseObserver.onCompleted()
        }
    }

    override fun login(request: LoginRequest, responseObserver: StreamObserver<AuthResponse>) {
        val authResult = authenticator.login(request.login, request.password)
        //TODO: запомнить факт логина где-нибудь в БД

        responseObserver.onNext(createAuthResponse(authResult))
        responseObserver.onCompleted()
    }
}
