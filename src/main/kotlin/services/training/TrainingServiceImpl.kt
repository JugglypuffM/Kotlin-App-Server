package services.training

import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.auth.ResultCode
import domain.training.Training
import grpc.TrainingProto
import grpc.TrainingServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import services.auth.Authenticator
import java.time.LocalDate
import java.util.Optional

class TrainingServiceImpl(private val authenticator: Authenticator, private val databaseManager: DatabaseManager) :
    TrainingServiceGrpc.TrainingServiceImplBase() {
    override fun saveTraining(request: TrainingProto.SaveRequest, responseObserver: StreamObserver<Empty>) {
        when (authenticator.login(request.login, request.password).resultCode) {
            ResultCode.OPERATION_SUCCESS -> {
                val training: Optional<Training> = when {
                    request.training.hasYoga() -> Optional.of(Training.Yoga(request.training.yoga))

                    request.training.hasJogging() -> Optional.of(Training.Jogging(request.training.jogging))

                    else -> Optional.empty()
                }

                training.ifPresentOrElse(
                    {
                        databaseManager.saveTraining(request.login, it)
                        responseObserver.onNext(Empty.getDefaultInstance())
                    },
                    {
                        responseObserver.onError(
                            Status.INVALID_ARGUMENT.withDescription("Unexpected training type while parsing training")
                                .asRuntimeException()
                        )
                    }
                )
            }

            else -> responseObserver.onError(
                Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException()
            )
        }
        responseObserver.onCompleted()
    }

    override fun getTrainings(
        request: TrainingProto.TrainingsRequest,
        responseObserver: StreamObserver<TrainingProto.TrainingsResponse>
    ) {
        when (authenticator.login(request.login, request.password).resultCode) {
            ResultCode.OPERATION_SUCCESS -> {
                val trainings =
                    databaseManager.getTrainingsOnDate(request.login, LocalDate.ofEpochDay(request.date.seconds))
                val protoTrainings = trainings.map { it.toTrainingProto() }
                responseObserver.onNext(
                    TrainingProto.TrainingsResponse.newBuilder().addAllTrainings(protoTrainings).build()
                )
            }

            else -> responseObserver.onError(
                Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException()
            )
        }
        responseObserver.onCompleted()
    }
}