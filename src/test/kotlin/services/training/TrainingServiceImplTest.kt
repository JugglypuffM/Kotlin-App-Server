package services.training

import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.auth.AuthResult
import domain.auth.ResultCode
import domain.training.Training
import grpc.TrainingProto
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.auth.AuthenticatorInterface
import java.time.Duration
import java.time.LocalDate

class TrainingServiceImplTest {

    private lateinit var authenticator: AuthenticatorInterface
    private lateinit var databaseManager: DatabaseManager
    private lateinit var service: TrainingServiceImpl

    @BeforeEach
    fun setUp() {
        authenticator = mockk()
        databaseManager = mockk()
        service = TrainingServiceImpl(authenticator, databaseManager)
    }

//    @Test
//    fun `saveTraining - should save training successfully`() {
//        val date = LocalDate.now()
//        // Arrange
//        val request = TrainingProto.SaveRequest.newBuilder()
//            .setLogin("user1")
//            .setPassword("password")
//            .setTraining(
//                TrainingProto.Training.newBuilder()
//                    .setYoga(
//                        TrainingProto.Yoga.newBuilder()
//                            .setDate(com.google.protobuf.Timestamp.newBuilder().setSeconds(date.toEpochDay())) // Example date
//                            .setDuration(com.google.protobuf.Duration.newBuilder().setSeconds(3600)) // 1 hour
//                            .build()
//                    )
//                    .build()
//            )
//            .build()
//
//        val responseObserver = mockk<StreamObserver<Empty>>(relaxed = true)
//
//        every { authenticator.login("user1", "password") } returns AuthResult(ResultCode.OPERATION_SUCCESS, "Success")
//        every { databaseManager.saveTraining("user1", Training.Yoga(date, Duration.ofSeconds(3600))) } just Runs
//
//        // Act
//        service.saveTraining(request, responseObserver)
//
//        // Assert
//        verify { databaseManager.saveTraining("user1", eq(Training.Yoga(date, Duration.ofSeconds(3600)))) }
//        verify { responseObserver.onNext(Empty.getDefaultInstance()) }
//        verify { responseObserver.onCompleted() }
//    }

    @Test
    fun `saveTraining - should fail with UNAUTHENTICATED if credentials are invalid`() {
        // Arrange
        val request = TrainingProto.SaveRequest.newBuilder()
            .setLogin("user1")
            .setPassword("wrongPassword")
            .build()

        val responseObserver = mockk<StreamObserver<Empty>>(relaxed = true)

        every { authenticator.login("user1", "wrongPassword") } returns AuthResult(
            ResultCode.INVALID_CREDENTIALS,
            "Invalid credentials"
        )

        // Act
        service.saveTraining(request, responseObserver)

        // Assert
        verify {
            responseObserver.onError(withArg {
                assert(it is StatusRuntimeException)
                assert(it.message == "UNAUTHENTICATED: Invalid credentials")
            })
        }
    }

    @Test
    fun `getTrainings - should return trainings successfully`() {
        // Arrange
        val date = LocalDate.of(2024, 12, 6)
        val request = TrainingProto.TrainingsRequest.newBuilder()
            .setLogin("user1")
            .setPassword("password")
            .setDate(com.google.protobuf.Timestamp.newBuilder().setSeconds(date.toEpochDay()))
            .build()

        val trainings = listOf(Training.Yoga(date, Duration.ofSeconds(1000)), Training.Jogging(date, Duration.ofSeconds(1000), 1.0))
        val responseObserver = mockk<StreamObserver<TrainingProto.TrainingsResponse>>(relaxed = true)

        every { authenticator.login("user1", "password") } returns AuthResult(ResultCode.OPERATION_SUCCESS, "Success")
        every { databaseManager.getTrainingsOnDate("user1", date) } returns trainings

        // Act
        service.getTrainings(request, responseObserver)

        // Assert
        verify {
            responseObserver.onNext(withArg {
                assert(it.trainingsCount == 2)
            })
            responseObserver.onCompleted()
        }
    }

    @Test
    fun `getTrainings - should fail with UNAUTHENTICATED if credentials are invalid`() {
        // Arrange
        val request = TrainingProto.TrainingsRequest.newBuilder()
            .setLogin("user1")
            .setPassword("wrongPassword")
            .build()

        val responseObserver = mockk<StreamObserver<TrainingProto.TrainingsResponse>>(relaxed = true)

        every { authenticator.login("user1", "wrongPassword") } returns AuthResult(
            ResultCode.INVALID_CREDENTIALS,
            "Invalid credentials"
        )

        // Act
        service.getTrainings(request, responseObserver)

        // Assert
        verify {
            responseObserver.onError(withArg {
                assert(it is StatusRuntimeException)
                assert(it.message == "UNAUTHENTICATED: Invalid credentials")
            })
        }
    }
}
