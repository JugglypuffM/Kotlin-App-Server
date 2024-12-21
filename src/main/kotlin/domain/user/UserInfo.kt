package domain.user

import grpc.DataProto.UserData

/**
 * Класс с информацией о пользователе
 */
data class UserInfo(
    val name: String,
    val age: Int,
    val weight: Int,
    val distance: Int,
) {
    constructor(userData: UserData) : this(
        userData.name,
        userData.age,
        userData.weight,
        userData.totalDistance
    )

    fun toUserData() = UserData.newBuilder()
        .setName(name)
        .setAge(age)
        .setWeight(weight)
        .setTotalDistance(distance)
        .build()
}
