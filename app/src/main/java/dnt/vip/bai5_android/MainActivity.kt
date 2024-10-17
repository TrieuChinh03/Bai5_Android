package dnt.vip.bai5_android

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dnt.vip.bai5_android.service.ReminderReceiver
import dnt.vip.bai5_android.ui.screen.Screens
import dnt.vip.bai5_android.ui.screen.TaskListScreen
import java.util.Calendar

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

    //===   Tạo lịch thông báo      ===
    private fun createReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        //---   Thời gian thông báo  ---
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        //---   Nếu đã qua 6h -> lên lịch cho ngày hôm sau  ---
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        //---   Thiết lập lịch hàng ngày    ---
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
