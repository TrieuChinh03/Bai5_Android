package dnt.vip.bai5_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dnt.vip.bai5_android.service.ReminderWorker
import dnt.vip.bai5_android.ui.screen.Screens
import dnt.vip.bai5_android.ui.screen.TaskListScreen
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Surface(color = MaterialTheme.colorScheme.background) {
                NavHost(navController, startDestination = Screens.TASK_LIST) {
                    composable(Screens.TASK_LIST) { TaskListScreen() }
                }
            }
        }

        createReminder()
    }

    //===   Hàm tạo lịch thông báo    ===
    private fun createReminder() {
        val reminderWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(setNotificationTime(), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "reminder_task",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWorkRequest.build()
        )
    }

    //===   Hàm đặt thời gian thông báo    ===
    private fun setNotificationTime(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (now.after(target)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}
