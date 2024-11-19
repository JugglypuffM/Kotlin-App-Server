package data

import auth.Authenticator
import database.Database
import domain.ResultCode
import domain.User
import grpc.DataProto.BasicDataRequest
import grpc.DataProto.BasicDataResponse
import grpc.DataServiceGrpc
import io.grpc.stub.StreamObserver

class DataServiceImpl(val authenticator: Authenticator, val userDatabase: Database<User>) :
    DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, name: String): BasicDataResponse {
        return BasicDataResponse.newBuilder()
            .setSuccess(success)
            .setName(name)
            .build()
    }

    override fun getBasicUserData(
        request: BasicDataRequest,
        responseObserver: StreamObserver<BasicDataResponse>
    ) {
        val authResult = authenticator.login(request.login, request.password)
        when (authResult.resultCode) {
            ResultCode.OPERATION_SUCCESS, ResultCode.USER_ALREADY_EXISTS ->
                responseObserver.onNext(createBasicDataResponse(true, userDatabase.get(request.login).get().name))

            ResultCode.INVALID_CREDENTIALS ->
                responseObserver.onNext(createBasicDataResponse(false, ""))
        }
        responseObserver.onCompleted()
    }
}
