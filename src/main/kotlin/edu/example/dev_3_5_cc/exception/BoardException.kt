package edu.example.dev_3_5_cc.exception

enum class BoardException(val message: String, val code: Int) {
    INVALID_TITLE("Invalid Title", 400),
    INVALID_DESCRIPTION("Invalid Description", 400),
    INVALID_CATEGORY("Invalid Category", 400),
    NOT_FOUND("Board Not Found", 404),
    NOT_CREATED("Board Not Created", 400),
    NOT_UPDATED("Board Not Updated", 400),
    NOT_DELETED("Board Not Deleted", 400),
    IMAGE_NOT_FOUND("Image Not Found", 404);

    val boardTaskException: BoardTaskException = BoardTaskException(message, code)

    fun get(): BoardTaskException { return boardTaskException }
}