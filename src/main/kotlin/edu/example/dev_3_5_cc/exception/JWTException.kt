package edu.example.dev_3_5_cc.exception

class JWTException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}