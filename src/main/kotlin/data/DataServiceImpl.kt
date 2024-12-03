package data

import database.manager.DatabaseManager
import domain.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import grpc.DataServiceGrpc
import io.grpc.stub.StreamObserver

class DataServiceImpl(private val databaseManager: DatabaseManager) : DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, info: UserInfo): UserDataResponse {
        val data = DataProto.UserData.newBuilder()
            .setName(info.name)
            .setAge(info.age.takeIf { it!! > 0 } ?: 0)
            .setWeight(info.weight.takeIf { it!! > 0 } ?: 0)
            .setTotalDistance(info.distance.takeIf { it!! > 0 } ?: 0)
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
        databaseManager.getUserInformation(request.login).map<Unit> {
            responseObserver.onNext(createBasicDataResponse(true, it))
        }.orElse(responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0))))
        responseObserver.onCompleted()
    }
}
