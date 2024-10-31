package edu.example.dev_3_5_cc.exception

enum class MemberException(val message: String, val code: Int) {
    NOT_FOUND("NOT_FOUND", 404),
    DUPLICATE("DUPLICATE(중복되는 아이디가 존재합니다)", 409),
    INVALID("INVALID", 400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401),
    NOT_MODIFIED("NOT_MODIFIED", 405),
    NOT_REMOVED("NOT_REMOVED", 402);

    val memberTaskException: MemberTaskException = MemberTaskException(message, code)

    fun get(): MemberTaskException { return memberTaskException }

}