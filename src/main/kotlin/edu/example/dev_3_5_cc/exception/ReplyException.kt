package edu.example.dev_3_5_cc.exception

enum class ReplyException (val message: String, val code: Int){
    NOT_FOUND("Reply NOT_FOUND", 404),
    NOT_CREATED("Reply Not Created", 400),
    NOT_UPDATED("Reply Not Updated", 400),
    NOT_FETCHED("Reply Not Fetched", 400),
    NOT_DELETED("Reply Not Deleted", 400);

    val replyTaskException: ReplyTaskException = ReplyTaskException(message, code)

    fun get(): ReplyTaskException { return replyTaskException }
}