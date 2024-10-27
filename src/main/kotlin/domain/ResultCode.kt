package domain

enum class ResultCode(val code: Int) {
    OPERATION_SUCCESS(0),
    USER_ALREADY_EXISTS(1),
    INVALID_CREDENTIALS(2)
}