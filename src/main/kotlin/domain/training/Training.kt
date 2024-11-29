package domain.training

import grpc.TrainingProto
import java.time.Duration
import java.time.LocalDate

/**
 * Информация о тренировке
 * Специфична для каждого типа тренировки
 */
sealed class Training(
    val date: LocalDate,
    val duration: Duration
) {
    /**
     * Информация о йоге
     */
    class Yoga(date: LocalDate, duration: Duration) : Training(
        date = date,
        duration = duration
    ) {
        constructor(yoga: TrainingProto.Yoga) : this(
            LocalDate.ofEpochDay(yoga.date.seconds),
            Duration.ofSeconds(yoga.duration.seconds)
        )

        override fun toTrainingProto(): TrainingProto.Training {
            TODO("Not yet implemented")
        }
    }

    /**
     * Информация о беге
     */
    class Jogging(date: LocalDate, duration: Duration, val distance: Double) : Training(
        date = date,
        duration = duration
    ){
        constructor(jogging: TrainingProto.Jogging) : this(
            LocalDate.ofEpochDay(jogging.date.seconds),
            Duration.ofSeconds(jogging.duration.seconds),
            jogging.distance
        )

        override fun toTrainingProto(): TrainingProto.Training {
            TODO("Not yet implemented")
        }
    }

    abstract fun toTrainingProto(): TrainingProto.Training
}