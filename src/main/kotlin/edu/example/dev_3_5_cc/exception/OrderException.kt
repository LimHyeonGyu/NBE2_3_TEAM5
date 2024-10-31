package edu.example.dev_3_5_cc.exception

enum class OrderException(val message: String, val code: Int) {
    NOT_FOUND("NOT_FOUND", 404),
    NOT_REGISTERED("NOT_REGISTERED", 400),
    NOT_MODIFIED("NOT_MODIFIED", 400),
    NOT_REMOVED("NOT_REMOVED", 400),
    DUPLICATE("DUPLICATE", 409),
    INVALID("INVALID", 400),
    NOT_ENOUGH_STOCK("NOT_ENOUGH_STOCK", 400),
    ALREADY_DELIVERED("ALREADY_DELIVERED", 400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401);

    val orderTaskException: OrderTaskException = OrderTaskException(message, code)

    fun get(): OrderTaskException { return orderTaskException }
}