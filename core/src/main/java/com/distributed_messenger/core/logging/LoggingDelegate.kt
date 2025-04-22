package com.distributed_messenger.core.logging

import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

class LoggingDelegate<T : Any>(
    val origin: T,
    val logger: Logger,
    val tag: String = origin::class.simpleName ?: "Unknown"
) {
    suspend inline operator fun <reified R> invoke(
        noinline block: suspend T.() -> R
    ): R {
        val methodName = getMethodName(block)
        logger.log(tag, "Calling $methodName", LogLevel.DEBUG)

        return try {
            val result = origin.block()
            logger.log(tag, "$methodName succeeded: $result", LogLevel.DEBUG)
            result
        } catch (e: Exception) {
            logger.log(tag, "$methodName failed: ${e.message}", LogLevel.ERROR, e)
            throw e
        }
    }

    fun <R> getMethodName(block: suspend T.() -> R): String {
        return (block as? KFunction<*>)?.javaMethod?.name ?: "unknown_method"
    }
}