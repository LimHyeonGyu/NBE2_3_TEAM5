package edu.example.dev_3_5_cc.exception

class CartTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
