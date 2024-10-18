package auth

import domain.AuthResult
import domain.User
import io.mockk.*
import io.grpc.stub.StreamObserver
import org.example.grpc.AuthProto.AuthResponse
import org.example.grpc.AuthProto.LoginRequest
import org.example.grpc.AuthProto.RegisterRequest
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
        val user = User("John Doe", "johndoe", "password123")
        val request = RegisterRequest.newBuilder()
            .setName(user.name)
            .setLogin(user.login)
            .setPassword(user.password)
            .build()

        every { authenticator.register(user) } returns AuthResult(true, "User successfully registered.")

        authService.register(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setSuccess(true).setMessage("User successfully registered.").build()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `register - user already exists`() {
        val user = User("John Doe", "johndoe", "password123")
        val request = RegisterRequest.newBuilder()
            .setName(user.name)
            .setLogin(user.login)
            .setPassword(user.password)
            .build()

        every { authenticator.register(user) } returns AuthResult(false, "User already exists.")

        authService.register(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setSuccess(false).setMessage("User already exists.").build()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `register - invalid password (too short)`() {
        val request = RegisterRequest.newBuilder()
            .setName("John Doe")
            .setLogin("johndoe")
            .setPassword("pass")
            .build()

        authService.register(request, responseObserver)

        verify {
            responseObserver.onNext(AuthResponse.newBuilder()
                .setSuccess(false)
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

        every { authenticator.login(login, password) } returns AuthResult(true, "User successfully logged in.")

        authService.login(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setSuccess(true).setMessage("User successfully logged in.").build()) }
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

        every { authenticator.login(login, password) } returns AuthResult(false, "Invalid login or password.")

        authService.login(request, responseObserver)

        verify { responseObserver.onNext(AuthResponse.newBuilder().setSuccess(false).setMessage("Invalid login or password.").build()) }
        verify { responseObserver.onCompleted() }
    }
}
