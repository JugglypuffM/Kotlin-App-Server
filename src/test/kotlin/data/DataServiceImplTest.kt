package data

import services.auth.Authenticator
import database.manager.DatabaseManager
import domain.auth.AuthResult
import domain.auth.ResultCode
import domain.user.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import io.grpc.stub.StreamObserver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.data.DataServiceImpl
import java.util.Optional

class DataServiceImplTest {

    private lateinit var authenticatorMock: Authenticator
    private lateinit var databaseManagerMock: DatabaseManager
    private lateinit var dataServiceImpl: DataServiceImpl
    private lateinit var responseObserver: StreamObserver<UserDataResponse>

    @BeforeEach
    fun setUp() {
        authenticatorMock = mockk()
        databaseManagerMock = mockk()
        responseObserver = mockk(relaxed = true)
        dataServiceImpl = DataServiceImpl(authenticatorMock, databaseManagerMock)
    }

    @Test
    fun `getBasicUserData should return success when authentication is successful`() {
        val request = UserDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("testPass")
            .build()
        val userInfo = UserInfo("testName", 20, 80, 100)

        val expectedData = DataProto.UserData.newBuilder()
            .setName("testName")
            .setAge(20)
            .setWeight(80)
            .setTotalDistance(100)
            .build()

        every { authenticatorMock.login("testUser", "testPass") } returns AuthResult(ResultCode.OPERATION_SUCCESS, "")
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(userInfo)

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(true).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `getBasicUserData should return failure when credentials are invalid`() {
        val request = UserDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("wrongPass")
            .build()

        val expectedData = DataProto.UserData.newBuilder()
            .setName("")
            .setAge(0)
            .setWeight(0)
            .setTotalDistance(0)
            .build()

        every { authenticatorMock.login("testUser", "wrongPass") } returns AuthResult(ResultCode.INVALID_CREDENTIALS, "")
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(UserInfo("John", 1000-7, 993-7, 986-7))

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(false).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }
}
