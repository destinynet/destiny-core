package destiny.tools.ai

import kotlin.time.Duration

data class ResultDto<T>(val result: T,
                        val think: String?,
                        val provider: Provider,
                        val model: String,
                        val invokedFunCalls: List<FunCall> = emptyList(),
                        val inputTokens: Int?,
                        val outputTokens: Int?,
                        val duration: Duration?)
