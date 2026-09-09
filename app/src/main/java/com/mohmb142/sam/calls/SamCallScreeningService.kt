package com.mohmb142.sam.calls

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.core.app.NotificationCompat

/** Lightweight caller-identification layer. No network call is made in the 5-second screening window. */
class SamCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSilenceCall(false)
            .build()
        respondToCall(callDetails, response)

        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
            val number = callDetails.handle?.schemeSpecificPart ?: "رقم غير معروف"
            notifyCaller(number)
        }
    }

    private fun notifyCaller(number: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sam_calls"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "مكالمات SAM", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.sym_action_call)
            .setContentTitle("مكالمة واردة")
            .setContentText("المتصل: $number")
            .setAutoCancel(true)
            .build()
        manager.notify(number.hashCode(), notification)
    }
}
