package edu.example.dev_3_5_cc.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EvictCacheAfterExecution(val cacheName: String, val keyExpression: String)