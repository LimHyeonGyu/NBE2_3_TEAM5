package edu.example.dev_3_5_cc.aspect

import edu.example.dev_3_5_cc.annotation.EvictCacheAfterExecution
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.cache.CacheManager
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Aspect
@Component
class CacheEvictAspect(private val cacheManager: CacheManager) {

    @AfterReturning(value = "@annotation(evictCacheAfterExecution)", returning = "result")
    fun evictCache(joinPoint: JoinPoint, evictCacheAfterExecution: EvictCacheAfterExecution, result: Any?) {
        // 캐시 이름과 키 표현식 가져오기
        val cacheName = evictCacheAfterExecution.cacheName
        val keyExpression = evictCacheAfterExecution.keyExpression

        // SpEL 표현식을 평가하여 캐시 키를 추출
        val parser = SpelExpressionParser()
        val context = StandardEvaluationContext()
        context.setRootObject(joinPoint.args)

        // SpEL 표현식 평가 결과로 boardId 값 추출
        val boardId = parser.parseExpression(keyExpression).getValue(context, result, Long::class.java)

        // boardId가 null 이 아닐 때만 캐시에서 해당 항목 삭제
        boardId?.let {
            cacheManager.getCache(cacheName)?.evict(it)
        }
    }
}