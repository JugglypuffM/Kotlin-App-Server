package database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.date
import org.joda.time.DateTime
import java.math.BigDecimal

object TrainingTable : LongIdTable() {
    val loginID = optReference(name="FK_loginID", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val workoutType: Column<String> = varchar("type", 20)
    val workoutDate: Column<DateTime> = date("workout_date")
    val workoutDuration: Column<Long> = long("workout_duration")
    val distance: Column<BigDecimal?> = decimal("distance", 2, 7).nullable()
    //Более подробная информация о тренировке: ссылка, описание и т.д.
    val additionalData: Column<String?> = varchar("additional_data", 255).nullable()
}