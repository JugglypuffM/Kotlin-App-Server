package database.dao

import database.exception.DatabaseException
import database.tables.TrainingTable
import database.tables.UsersTable
import domain.training.Training
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.LocalDate

class TrainingDAO{

    init {
        transaction {
            try {
                SchemaUtils.create(TrainingTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }
    fun get(login: String, date: java.time.LocalDate): List<Training> {
        val trainingList: MutableList<Training> = mutableListOf()
        var training : Training
        transaction {
            try {
                val userId : Int = TrainingTable.select(UsersTable.id)
                    .limit(1)
                    .where {UsersTable.login.eq(login)}
                    .map { it[UsersTable.id] }
                    .single().value
                for (entry in TrainingTable.selectAll().where {
                    (TrainingTable.loginID.eq(userId)) and (TrainingTable.workoutDate.date()
                        .eq(LocalDate(date.year, date.monthValue, date.dayOfMonth).toDateTimeAtStartOfDay()))}) {
                    val dateTime = entry[TrainingTable.workoutDate]
                    when (entry[TrainingTable.workoutType]) {
                        "Jogging" -> {
                            training = Training.Jogging(
                                id = entry[TrainingTable.id].value,
                                date = java.time.LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth),
                                duration = java.time.Duration.ofSeconds(entry[TrainingTable.workoutDuration]),
                                distance = entry[TrainingTable.distance] ?: 0
                            )
                            trainingList.add(training)
                        }
                        "Yoga" -> {
                            training = Training.Yoga(
                                id = entry[TrainingTable.id].value,
                                date = java.time.LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth),
                                duration = java.time.Duration.ofSeconds(entry[TrainingTable.workoutDuration])
                                )
                            trainingList.add(training)
                        }
                    }
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return trainingList
    }

    fun delete(id: Long) {
        transaction {
            try {
                TrainingTable.deleteWhere { TrainingTable.id.eq(id) }
            } catch (e: Exception) {
                throw DatabaseException("The training was not found: $id", e)
            }
        }
    }

    fun add(login: String, training: Training) {
        transaction {
            try {
                TrainingTable.insert {
                    it[loginID] = select(UsersTable.id)
                        .limit(1)
                        .where {UsersTable.login.eq(login)}
                        .map { it[UsersTable.id] }
                        .single().value
                    it[workoutDate] = DateTime(training.date)
                    it[workoutDuration] = training.duration.seconds
                    when (training) {
                        is Training.Jogging -> {
                            it[workoutType] = "Jogging"
                            it[distance] = training.distance
                        }
                        is Training.Yoga -> it[workoutType] = "Yoga"
                    }
                }
            } catch (e: Exception) {
                throw DatabaseException("Error adding user with id: $id", e)
            }
        }
    }
}