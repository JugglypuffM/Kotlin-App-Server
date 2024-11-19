package data

import database.manager.DatabaseManager
import domain.UserInfo
import grpc.DataProto
import grpc.DataProto.BasicDataRequest
import grpc.DataProto.BasicDataResponse
import grpc.DataServiceGrpc
import io.grpc.stub.StreamObserver

class DataServiceImpl(private val databaseManager: DatabaseManager) : DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, info: UserInfo): BasicDataResponse {
        val data = DataProto.UserData.newBuilder()
            .setName(info.name)
            .setAge(info.age)
            .setWeight(info.weight)
            .setTotalDistance(info.distance)
            .build()
        return BasicDataResponse.newBuilder()
            .setSuccess(success)
            .setData(data)
            .build()
    }

    override fun getBasicUserData(
        request: BasicDataRequest,
        responseObserver: StreamObserver<BasicDataResponse>
    ) {
        databaseManager.getUserInformation(request.login).map<Unit> {
            responseObserver.onNext(createBasicDataResponse(true, it))
        }.orElse(responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0))))
        responseObserver.onCompleted()
    }
}
