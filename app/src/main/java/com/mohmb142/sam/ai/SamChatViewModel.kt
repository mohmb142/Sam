package com.mohmb142.sam.ai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SamChatViewModel(app: Application) : AndroidViewModel(app) {
    private val store = ModelStore(app)
    private val router = ModelRouter(mapOf(OpenRouterDefaults.PROVIDER to OpenAiCompatibleProvider()))
    private val _messages = MutableStateFlow(listOf("مرحباً، أنا سام. كيف أساعدك؟"))
    val messages: StateFlow<List<String>> = _messages.asStateFlow()
    private val _models = MutableStateFlow(store.getAll())
    val models: StateFlow<List<ModelConfig>> = _models.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    fun saveOpenRouter(apiKey: String, model: String) {
        val existing = _models.value.firstOrNull { it.provider == OpenRouterDefaults.PROVIDER }
        val config = ModelConfig(
            id = existing?.id ?: UUID.randomUUID().toString(),
            provider = OpenRouterDefaults.PROVIDER,
            apiKey = apiKey.trim(),
            baseUrl = OpenRouterDefaults.BASE_URL,
            modelName = model.trim().ifBlank { OpenRouterDefaults.DEFAULT_MODEL },
            capabilities = setOf("chat", "agent"),
            enabled = true
        )
        store.save(config)
        _models.value = store.getAll()
    }

    fun send(text: String) {
        if (text.isBlank() || _busy.value) return
        _messages.value = _messages.value + "أنت: $text"
        val config = _models.value.firstOrNull { it.provider == OpenRouterDefaults.PROVIDER && it.enabled && it.apiKey.isNotBlank() }
        if (config == null) {
            _messages.value = _messages.value + "سام: أضف مفتاح OpenRouter من «النماذج» أولاً."
            return
        }
        _busy.value = true
        viewModelScope.launch {
            val result = router.route(
                AIRequest(
                    system = "أنت سام، مساعد شخصي عربي. أجب باختصار ووضوح، ولا تدّعي تنفيذ إجراء على الهاتف قبل تنفيذه فعلياً.",
                    user = text
                ), config
            )
            result.onSuccess { response ->
                _messages.value = _messages.value + "سام: ${response.text}"
            }.onFailure { error ->
                _messages.value = _messages.value + "سام: تعذر الاتصال بالنموذج: ${error.message ?: "خطأ غير معروف"}"
            }
            _busy.value = false
        }
    }

    fun clearMessages() { _messages.value = listOf("مرحباً، أنا سام. كيف أساعدك؟") }
}
