import services.auth.AuthServiceImpl
import services.auth.Authenticator
import services.data.DataServiceImpl
import database.manager.DatabaseManagerService
import io.grpc.Server
import io.grpc.ServerBuilder

fun main() {
    val authenticator = Authenticator(DatabaseManagerService)

    val server: Server = ServerBuilder.forPort(50051)
        .addService(AuthServiceImpl(authenticator))
        .addService(DataServiceImpl(authenticator, DatabaseManagerService))
        .build()
        .start()

    println("gRPC server is running on port 50051")
    server.awaitTermination()
}