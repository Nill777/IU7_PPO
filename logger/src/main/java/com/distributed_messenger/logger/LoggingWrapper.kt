package com.distributed_messenger.logger

class LoggingWrapper<T : Any>(
    val origin: T,
    val logger: ILogger,
    val tag: String = origin::class.simpleName ?: "Unknown"
) {
    suspend inline operator fun <reified R> invoke(
        explicitMethodName: String? = null,
        noinline block: suspend T.() -> R
    ): R {
        val methodName = explicitMethodName?: getMethodName(block)
        logger.log(tag, "Calling $methodName", LogLevel.INFO)

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
        val stackTrace = Throwable().stackTrace

        return stackTrace.asSequence()
            .filterNot {
                // Исключаем классы логирования и Kotlin runtime
                it.className.startsWith("com.distributed_messenger.logger") ||
                        it.className.startsWith("kotlin.coroutines") ||
                        it.className.startsWith("kotlin.reflect")
            }
            .firstOrNull()
            ?.let { element ->
                element.methodName
                    .substringBefore("\$") // Убираем лямбда-суффиксы
                    .substringBefore("$") // Для Java synthetic методов
                    .replace("invokeSuspend", "")
                    .replace("invoke", "")
                    .takeIf { it.isNotBlank() }
            } ?: "unknown_method"
    }
}