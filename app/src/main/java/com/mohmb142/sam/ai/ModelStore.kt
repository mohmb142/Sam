package com.mohmb142.sam.ai

import android.content.Context
import com.mohmb142.sam.security.SecretStore
import org.json.JSONArray
import org.json.JSONObject

class ModelStore(context: Context) {
    private val prefs = context.getSharedPreferences("sam_models", Context.MODE_PRIVATE)
    private val secrets = SecretStore(context)

    fun save(config: ModelConfig) {
        val ids = prefs.getStringSet("ids", emptySet()).orEmpty().toMutableSet()
        ids += config.id
        prefs.edit()
            .putStringSet("ids", ids)
            .putString("model_${config.id}", JSONObject().apply {
                put("id", config.id)
                put("provider", config.provider)
                put("baseUrl", config.baseUrl)
                put("modelName", config.modelName)
                put("capabilities", JSONArray(config.capabilities.toList()))
                put("enabled", config.enabled)
            }.toString())
            .apply()
        secrets.putApiKey(config.id, config.apiKey)
    }

    fun getAll(): List<ModelConfig> = prefs.getStringSet("ids", emptySet()).orEmpty().mapNotNull { id ->
        val raw = prefs.getString("model_$id", null) ?: return@mapNotNull null
        runCatching {
            val j = JSONObject(raw)
            val caps = mutableSetOf<String>()
            val a = j.optJSONArray("capabilities")
            if (a != null) for (i in 0 until a.length()) caps += a.optString(i)
            ModelConfig(
                id = j.getString("id"), provider = j.getString("provider"),
                apiKey = secrets.getApiKey(id).orEmpty(), baseUrl = j.getString("baseUrl"),
                modelName = j.getString("modelName"), capabilities = caps,
                enabled = j.optBoolean("enabled", true)
            )
        }.getOrNull()
    }

    fun delete(id: String) {
        val ids = prefs.getStringSet("ids", emptySet()).orEmpty().toMutableSet()
        ids.remove(id)
        prefs.edit().putStringSet("ids", ids).remove("model_$id").apply()
        secrets.removeApiKey(id)
    }
}
