package services.auth

import domain.user.Account
import domain.auth.AuthResult
import domain.auth.ResultCode
import grpc.AuthProto.AuthResponse
import grpc.AuthProto.LoginRequest
import grpc.AuthProto.RegisterRequest
import io.grpc.stub.StreamObserver
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthServiceTest {

    private lateinit var authenticator: Authenticator
    private lateinit var authService: AuthServiceImpl
    private lateinit var responseObserver: StreamObserver<AuthResponse>

    @BeforeEach
    fun setup() {
        authenticator = mockk()
        responseObserver = mockk(relaxed = true)

        authService = AuthServiceImpl(authenticator)
    }

    @Test
    fun `register - successful registration`() {
        val account = Account("johndoe", "password123")
        val request = RegisterRequest.newBuilder()
            .setLogin(account.login)
            .setPassword(account.password)
            .build()

        every { authenticator.register(account) } returns AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully registered.")

        authService.register(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setResultCode(0).setMessage("User successfully registered.").build()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `register - user already exists`() {
        val account = Account("johndoe", "password123")
        val request = RegisterRequest.newBuilder()
            .setLogin(account.login)
            .setPassword(account.password)
            .build()

        every { authenticator.register(account) } returns AuthResult(ResultCode.USER_ALREADY_EXISTS, "User already exists.")

        authService.register(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setResultCode(1).setMessage("User already exists.").build()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `register - invalid password (too short)`() {
        val request = RegisterRequest.newBuilder()
            .setLogin("johndoe")
            .setPassword("pass")
            .build()

        authService.register(request, responseObserver)

        verify {
            responseObserver.onNext(AuthResponse.newBuilder()
                .setResultCode(2)
                .setMessage("Invalid login or password.")
                .build())
        }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `login - successful login`() {
        val login = "johndoe"
        val password = "password123"
        val request = LoginRequest.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .build()

        every { authenticator.login(login, password) } returns AuthResult(ResultCode.OPERATION_SUCCESS, "User successfully logged in.")

        authService.login(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setResultCode(0).setMessage("User successfully logged in.").build()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `login - invalid login or password`() {
        val login = "johndoe"
        val password = "wrongpassword"
        val request = LoginRequest.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .build()

        every { authenticator.login(login, password) } returns AuthResult(ResultCode.INVALID_CREDENTIALS, "Wrong login or password.")

        authService.login(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setResultCode(2).setMessage("Wrong login or password.").build()) }
        verify { responseObserver.onCompleted() }
    }
}
