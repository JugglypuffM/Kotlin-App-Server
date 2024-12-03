package database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.datetime
import java.time.LocalDateTime

object TrainingTable : IntIdTable() {
    val userLogin: Column<String> = varchar("user_login", 45).references(UsersTable.login)
    val workoutDate: Column<LocalDateTime> = datetime("workout_date")
    val workoutDuration: Column<Int> = integer("workout_duration") // продолжительность в минутах
    val additionalData: Column<String?> = varchar("additional_data", 255).nullable() // может быть null
}