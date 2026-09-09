package com.mohmb142.sam.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Works with OpenAI-compatible APIs such as OpenRouter and many custom gateways. */
class OpenAiCompatibleProvider : AIProvider {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override suspend fun generate(request: AIRequest, config: ModelConfig): Result<AIResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(config.apiKey.isNotBlank()) { "مفتاح API غير موجود" }
                require(config.modelName.isNotBlank()) { "اسم النموذج غير موجود" }

                val base = config.baseUrl.trimEnd('/')
                val endpoint = if (base.endsWith("/chat/completions")) base else "$base/chat/completions"
                val messages = JSONArray()
                request.system?.takeIf { it.isNotBlank() }?.let {
                    messages.put(JSONObject().put("role", "system").put("content", it))
                }
                messages.put(JSONObject().put("role", "user").put("content", request.user))

                val body = JSONObject()
                    .put("model", config.modelName)
                    .put("messages", messages)
                    .toString()
                    .toRequestBody("application/json".toMediaType())

                val httpRequest = Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer ${config.apiKey}")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(httpRequest).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code}: ${raw.take(500)}")
                    }
                    val json = JSONObject(raw)
                    val text = json.optJSONArray("choices")
                        ?.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content")
                        .orEmpty()
                    require(text.isNotBlank()) { "استجابة النموذج فارغة" }
                    AIResponse(text)
                }
            }
        }
}
