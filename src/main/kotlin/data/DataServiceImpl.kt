package data

import auth.Authenticator
import database.manager.DatabaseManager
import domain.ResultCode
import domain.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import grpc.DataServiceGrpc
import io.grpc.stub.StreamObserver

class DataServiceImpl(private val authenticator: Authenticator, private val databaseManager: DatabaseManager) :
    DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, info: UserInfo): UserDataResponse {
        val data = DataProto.UserData.newBuilder()
            .setName(info.name)
            .setAge(info.age)
            .setWeight(info.weight)
            .setTotalDistance(info.distance)
            .build()
        return UserDataResponse.newBuilder()
            .setSuccess(success)
            .setData(data)
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

                responseObserver.onCompleted()
            }

            else -> responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0)))
        }
    }
}
