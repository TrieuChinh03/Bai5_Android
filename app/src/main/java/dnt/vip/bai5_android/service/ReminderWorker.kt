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

    companion object {
        const val CHANNEL_ID = "reminder_channel"
    }

    //---   Hàm thực thi    ---
    override fun doWork(): Result {

        //---   Tải dữ liệu từ bài 4    ---
        val tasks = mutableListOf<Task>()
        runBlocking {
            loadTasks(applicationContext.contentResolver, tasks)
        }

        //---   Xây dựng nội dung thông báo   ---
        if (tasks.isEmpty()) {
            showNotification("Không có công việc nào", "Bạn đang rảnh rỗi!")
        } else {
            val buildContent = StringBuilder()
            var index = 1

            tasks.forEach { task ->
                buildContent.append("\nCông việc $index: ${task.title}")
                index++
            }
            showNotification(
                "Bạn có ${tasks.size} công việc cần làm",
                buildContent.toString()
            )
        }

        return Result.success()
    }

    //===   Hàm hiển thị thông báo    ===
    private fun showNotification(title: String, content: String) {
        //---   Tạo kênh    ---
        createNotificationChannel(applicationContext)

        //---   Xây dựng thông báo    ---
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    //===   Hàm tạo kênh channel    ===
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Bai 5 Android",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for daily reminders"
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}