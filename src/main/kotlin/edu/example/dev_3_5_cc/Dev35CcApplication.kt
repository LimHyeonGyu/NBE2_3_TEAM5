package edu.example.dev_3_5_cc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
class Dev35CcApplication
inline val <reified T> T.log : Logger
    get() = LogManager.getLogger()

fun main(args: Array<String>) {
    runApplication<Dev35CcApplication>(*args)

}