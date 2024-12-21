package data

import auth.Authenticator
import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.ResultCode
import domain.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import grpc.DataServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver

class DataServiceImpl(private val authenticator: Authenticator, private val databaseManager: DatabaseManager) :
    DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, info: UserInfo): UserDataResponse {
        return UserDataResponse.newBuilder()
            .setSuccess(success)
            .setData(info.toUserData())
            .build()
    }

    override fun getUserData(
        request: UserDataRequest,
        responseObserver: StreamObserver<UserDataResponse>
    ) {
        val result = authenticator.login(request.login, request.password)
        when (result.resultCode) {
            ResultCode.OPERATION_SUCCESS -> {
                databaseManager.getUserInformation(request.login).map<Unit> {
                    responseObserver.onNext(createBasicDataResponse(true, it))
                }.orElse(responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0))))
            }

            else -> responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0)))
        }
        responseObserver.onCompleted()
    }

    override fun updateUserData(request: DataProto.UpdateDataRequest, responseObserver: StreamObserver<Empty>) {
        val result = authenticator.login(request.login, request.password)
        if (!request.hasData()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("No data provided").asRuntimeException())
            return
        }
        when (result.resultCode) {
            ResultCode.OPERATION_SUCCESS -> {
                databaseManager.updateUserInformation(request.login, UserInfo(request.data))
                responseObserver.onNext(Empty.getDefaultInstance())
                responseObserver.onCompleted()
            }

            else -> responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException())
        }
    }
}
