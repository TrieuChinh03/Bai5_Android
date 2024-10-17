package dnt.vip.bai5_android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dnt.vip.bai5_android.R
import dnt.vip.bai5_android.model.Task
import dnt.vip.bai5_android.provider.loadTasks
import kotlinx.coroutines.runBlocking

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "bai5"
    }

    override fun onReceive(context: Context, intent: Intent) {
        //---   Tải dữ liệu bài 4    ---
        val tasks = mutableListOf<Task>()
        runBlocking {
            loadTasks(context.contentResolver, tasks)
        }

        //---  Xây dựng thông báo   ---
        if (tasks.isEmpty()) {
            showNotification(context, "Không có công việc nào", "Bạn đang rảnh rỗi!")
        } else {
            val buildContent = StringBuilder()
            var index = 1

            tasks.forEach { task ->
                buildContent.append("\nCông việc $index: ${task.title}")
                index++
            }
            showNotification(
                context,
                "Bạn có ${tasks.size} công việc cần làm",
                buildContent.toString()
            )
        }
    }

    //===   Hàm hiển thị thông báo  ===
    private fun showNotification(context: Context, title: String, content: String) {
        createNotificationChannel(context)

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    //===   Hàm tạo kênh chanel   ===
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Nhắc nhở bài 5",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for daily reminders"
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
