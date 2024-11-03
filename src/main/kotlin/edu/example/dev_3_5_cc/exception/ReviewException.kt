package edu.example.dev_3_5_cc.exception

enum class ReviewException(message: String, code: Int) {
    ALREADY_EXISTS("Review ALREADY_EXISTS", 400),
    NOT_FOUND("Review NOT_FOUND", 404),
    NOT_CREATED("Review Not Created", 400),
    NOT_UPDATED("Review Not Updated", 400),
    NOT_FETCHED("Review Not Fetched", 400),
    NOT_DELETED("Review Not Deleted", 400);

    private val reviewTaskException = ReviewTaskException(message, code)

    fun get(): ReviewTaskException {
        return reviewTaskException
    }
}