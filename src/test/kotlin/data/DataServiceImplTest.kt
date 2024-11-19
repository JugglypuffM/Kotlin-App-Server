package data

import auth.Authenticator
import database.Database
import domain.ResultCode
import domain.User
import grpc.DataProto.BasicDataRequest
import grpc.DataProto.BasicDataResponse
import io.grpc.stub.StreamObserver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class DataServiceImplTest {

    private lateinit var authenticator: Authenticator
    private lateinit var userDatabase: Database<User>
    private lateinit var dataServiceImpl: DataServiceImpl
    private lateinit var responseObserver: StreamObserver<BasicDataResponse>

    @BeforeEach
    fun setUp() {
        authenticator = mockk()
        userDatabase = mockk()
        responseObserver = mockk(relaxed = true)
        dataServiceImpl = DataServiceImpl(authenticator, userDatabase)
    }

    @Test
    fun `getBasicUserData should return success when authentication is successful`() {
        val request = BasicDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("testPass")
            .build()
        val user = User("testName", "testUser", "testPass")

        every { authenticator.login("testUser", "testPass").resultCode } returns ResultCode.OPERATION_SUCCESS
        every { userDatabase.get("testUser") } returns Optional.of(user)

        dataServiceImpl.getBasicUserData(request, responseObserver)

        verify {responseObserver.onNext(BasicDataResponse.newBuilder().setSuccess(true).setName("testName").build()) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `getBasicUserData should return failure when credentials are invalid`() {
        val request = BasicDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("wrongPass")
            .build()

        every { authenticator.login("testUser", "wrongPass").resultCode } returns ResultCode.INVALID_CREDENTIALS

        dataServiceImpl.getBasicUserData(request, responseObserver)

        verify {responseObserver.onNext(BasicDataResponse.newBuilder().setSuccess(false).setName("").build()) }
        verify {responseObserver.onCompleted() }
    }
}
