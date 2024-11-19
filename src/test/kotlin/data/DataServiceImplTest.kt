package data

import auth.Authenticator
import database.manager.DatabaseManager
import domain.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import io.grpc.stub.StreamObserver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class DataServiceImplTest {

    private lateinit var authenticator: Authenticator
    private lateinit var databaseManagerMock: DatabaseManager
    private lateinit var dataServiceImpl: DataServiceImpl
    private lateinit var responseObserver: StreamObserver<UserDataResponse>

    @BeforeEach
    fun setUp() {
        authenticator = mockk()
        databaseManagerMock = mockk()
        responseObserver = mockk(relaxed = true)
        dataServiceImpl = DataServiceImpl(databaseManagerMock)
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

        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.empty<UserInfo>()

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(false).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }
}
