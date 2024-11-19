import auth.AuthServiceImpl
import auth.Authenticator
import data.DataServiceImpl
import database.DatabaseMock
import io.grpc.Server
import io.grpc.ServerBuilder

fun main() {
    val database = DatabaseMock()
    val authenticator = Authenticator(database)

    val server: Server = ServerBuilder.forPort(50051)
        .addService(AuthServiceImpl(authenticator))
        .addService(DataServiceImpl(authenticator, database))
        .build()
        .start()

    println("gRPC server is running on port 50051")
    server.awaitTermination()
}