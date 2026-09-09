package com.mohmb142.sam.memory

import android.content.Context
import org.json.JSONArray

class MemoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("sam_memory", Context.MODE_PRIVATE)
    fun add(text: String) {
        val value = text.trim()
        if (value.isEmpty()) return
        val current = JSONArray(prefs.getString(KEY, "[]"))
        for (i in 0 until current.length()) if (current.optString(i) == value) return
        current.put(value)
        prefs.edit().putString(KEY, current.toString()).apply()
    }
    fun all(): List<String> {
        val current = JSONArray(prefs.getString(KEY, "[]"))
        return List(current.length()) { i -> current.optString(i) }
    }
    fun clear() { prefs.edit().remove(KEY).apply() }
    companion object { private const val KEY = "items" }
}
