package edu.example.dev_3_5_cc.exception

data class MemberTaskException(override val message: String, val code: Int) : RuntimeException(message) {
}