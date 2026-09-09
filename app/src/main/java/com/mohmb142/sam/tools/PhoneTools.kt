package com.mohmb142.sam.tools

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import com.mohmb142.sam.agent.AgentAction
import com.mohmb142.sam.agent.ActionTypes
import com.mohmb142.sam.agent.Tool

class ContactsTool(private val context: Context) : Tool {
    override val id = ActionTypes.READ_CALLER
    override suspend fun execute(action: AgentAction): Result<String> = runCatching {
        val query = action.parameters["name"]?.trim().orEmpty()
        if (query.isEmpty()) return@runCatching "لم يتم تحديد اسم."
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)
        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, projection,
            "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?", arrayOf("%$query%"), null
        )?.use { c ->
            if (!c.moveToFirst()) "لم أجد جهة اتصال باسم $query."
            else "وجدت جهة الاتصال: ${c.getString(0)}"
        } ?: "تعذر قراءة جهات الاتصال."
    }
}

class AppLauncherTool(private val context: Context) : Tool {
    override val id = ActionTypes.OPEN_APP
    override suspend fun execute(action: AgentAction): Result<String> = runCatching {
        val packageName = action.parameters["package"] ?: return@runCatching "لم يتم تحديد التطبيق."
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return@runCatching "لا يمكن فتح التطبيق المطلوب."
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        "تم فتح التطبيق."
    }
}
