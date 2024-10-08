package dnt.vip.bai5_android.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import dnt.vip.bai5_android.R
import dnt.vip.bai5_android.model.Task
import dnt.vip.bai5_android.provider.loadTasks
import kotlinx.coroutines.runBlocking

class ReminderWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val tasks = mutableListOf<Task>()
        runBlocking {
            loadTasks(applicationContext.contentResolver, tasks)
        }

        if (tasks.isEmpty()) {
            showNotification("Không có công việc nào", "Bạn đang rảnh rỗi!")
        } else {
            tasks.forEach { task ->
                showNotification(task.title, task.content)
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        // Tạo Notification Channel (chỉ cần tạo một lần)
        createNotificationChannel(applicationContext)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay bằng icon của bạn
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Reminder Channel"
        val descriptionText = "Channel for daily reminders"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1
    }
}