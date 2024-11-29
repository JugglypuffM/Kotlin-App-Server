package domain.training

import grpc.TrainingProto
import java.time.Duration
import java.time.LocalDate

/**
 * Информация о тренировке
 * Специфична для каждого типа тренировки
 */
sealed class Training(
    val title: String,
    val description: String,
    val date: LocalDate,
    val duration: Duration
) {
    /**
     * Информация о йоге
     */
    class Yoga(date: LocalDate, duration: Duration) : Training(
        title = "Йога",
        description = "Йога помогает улучшить гибкость, снять стресс и укрепить мышечный корсет",
        date = date,
        duration = duration
    ) {
        constructor(yoga: TrainingProto.Yoga) : this(
            LocalDate.ofEpochDay(yoga.date.seconds),
            Duration.ofSeconds(yoga.duration.seconds)
        )
    }

    /**
     * Информация о беге
     */
    class Jogging(date: LocalDate, duration: Duration, val distance: Double) : Training(
        title = "Бег",
        description = "Бег способствует укреплению сердечно-сосудистой системы, улучшению выносливости и сжиганию лишних калорий",
        date = date,
        duration = duration
    ){
        constructor(jogging: TrainingProto.Jogging) : this(
            LocalDate.ofEpochDay(jogging.date.seconds),
            Duration.ofSeconds(jogging.duration.seconds),
            jogging.distance
        )
    }
}