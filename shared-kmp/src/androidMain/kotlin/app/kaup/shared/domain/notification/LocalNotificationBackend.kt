package app.kaup.shared.domain.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import app.kaup.shared.models.notification.NotificationEvent

class LocalNotificationBackend(private val context: Context) : NotificationBackend {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "kaup_alerts",
                "Kaup Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun scheduleLocalAlert(event: NotificationEvent) {
        if (event.targetTime == null) {
            // Fire immediately
            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, "kaup_alerts")
            } else {
                Notification.Builder(context)
            }

            builder.setContentTitle(event.title)
                .setContentText(event.message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Generic icon until UI module is built
            
            notificationManager.notify(event.id.hashCode(), builder.build())
        } else {
            // Schedule via AlarmManager
            val intent = Intent("app.kaup.ACTION_NOTIFICATION").apply {
                putExtra("id", event.id)
                putExtra("title", event.title)
                putExtra("message", event.message)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                event.id.hashCode(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                event.targetTime.toEpochMilliseconds(),
                pendingIntent
            )
        }
    }

    override fun cancelAlert(eventId: String) {
        notificationManager.cancel(eventId.hashCode())
        
        val intent = Intent("app.kaup.ACTION_NOTIFICATION")
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            eventId.hashCode(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun isRemoteCapable(): Boolean = false
}
