package edu.example.dev_3_5_cc.exception

class ReplyTaskException(override val message: String,val code:Int) : RuntimeException() {
}