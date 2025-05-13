package destiny.tools.ai

data class ResultDto<T>(val result: T, val provider: Provider, val model: String, val inputTokens: Int?, val outputTokens: Int?)
