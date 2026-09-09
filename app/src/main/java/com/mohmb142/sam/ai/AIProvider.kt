package com.mohmb142.sam.ai

data class ModelConfig(
    val id: String,
    val provider: String,
    val apiKey: String,
    val baseUrl: String,
    val modelName: String,
    val capabilities: Set<String> = emptySet(),
    val enabled: Boolean = true
)

data class AIRequest(val system: String?, val user: String)
data class AIResponse(val text: String)

interface AIProvider {
    suspend fun generate(request: AIRequest, config: ModelConfig): Result<AIResponse>
}

class ModelRouter(private val providers: Map<String, AIProvider>) {
    suspend fun route(request: AIRequest, config: ModelConfig): Result<AIResponse> {
        val provider = providers[config.provider]
            ?: return Result.failure(IllegalArgumentException("Provider غير مدعوم: ${config.provider}"))
        return provider.generate(request, config)
    }
}
