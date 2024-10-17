package dnt.vip.bai5_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.ContentResolver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dnt.vip.bai5_android.model.Task
import dnt.vip.bai5_android.provider.loadTasks
import dnt.vip.bai5_android.ui.theme.Background_Item
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

@Preview
@Composable
private fun TaskListScreenPreview() {
    TaskListScreen()
}

@Preview
@Composable
private fun TaskItemPreview() {
    TaskItem(task = Task(2, "Công việc 1", "Nội dung công việc 1" ,"21/09/2023"))
}

//Screen danh sách công việc    -------------------------------------------------------------
@Composable
fun TaskListScreen() {
    val tasks = remember { mutableStateListOf<Task>() }
    val current = remember { mutableIntStateOf(1) }

    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver

    LaunchedEffect(Unit) {
        loadTasks(contentResolver, tasks)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            //TEXT tiêu đề  ------------------------------------------------------
            Text(
                text = "Danh sách công việc hôm nay",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = "Hôm nay bạn rảnh!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                //LAZYCOLUMN danh sách công việc    --------------------------------------------
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tasks) { index, task ->
                        if (current.intValue < index) {
                            current.intValue = index
                            AdapterTast(task = task, animation = true, delay = 50L * index)
                        } else {
                            AdapterTast(task = task, animation = false, delay = 0)
                        }
                    }
                }
            }
        }
    }
}


/**     Adapter Item    **/
@Composable
fun AdapterTast(task: Task, animation: Boolean, delay: Long) {
    if(animation) {
        var isVisible by remember(task.id) { mutableStateOf(false) }
        LaunchedEffect(key1 = task.id) {
            delay(delay)
            isVisible = true
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 500)),
        ) {
            TaskItem(task)
        }
    }
    else {
        TaskItem(task)
    }
}

/**  Item công việc   **/
@Composable
fun TaskItem(task: Task) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Background_Item)
                .padding(12.dp)
        ) {
            //---   Tiêu đề công việc   ---
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            //---   Nội dung công việc   ---
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.content,
            )

            //---   Ngày thực hiện   ---
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.date.format(DateTimeFormatter.ofPattern("dd 'ngày' MM 'tháng' yyyy")),
            )
        }
    }
}