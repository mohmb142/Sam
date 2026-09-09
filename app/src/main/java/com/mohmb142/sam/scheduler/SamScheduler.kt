package com.mohmb142.sam.scheduler

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object SamScheduler {
    const val ACTION_TASK = "com.mohmb142.sam.SCHEDULED_TASK"
    fun schedule(context: Context, id: Int, triggerAtMillis: Long, text: String) {
        val intent = Intent(context, SamTaskReceiver::class.java).setAction(ACTION_TASK).putExtra("text", text)
        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
    }
}

class SamTaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel("sam_tasks", "SAM Tasks", NotificationManager.IMPORTANCE_DEFAULT))
        val text = intent.getStringExtra("text") ?: "لديك مهمة مجدولة."
        val notification = android.app.Notification.Builder(context, "sam_tasks")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("SAM").setContentText(text).setAutoCancel(true).build()
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}
