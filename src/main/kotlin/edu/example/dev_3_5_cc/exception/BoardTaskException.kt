package edu.example.dev_3_5_cc.exception

import java.lang.RuntimeException

class BoardTaskException (
    override val message : String,
    val code : Int
): RuntimeException(message){

}
