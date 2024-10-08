package dnt.vip.bai5_android.provider

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import dnt.vip.bai5_android.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//===   Load dữ liệu    ===
@SuppressLint("Range")
suspend fun loadTasks(contentResolver: ContentResolver, tasks: MutableList<Task>) {
    val authority = "dnt.kotlin.bai4_quanly_congviec.provider"
    val uri = Uri.parse("content://$authority/tasks")
    tasks.clear()

    withContext(Dispatchers.IO) {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val task = Task(
                    id = it.getLong(it.getColumnIndex("id")),
                    title = it.getString(it.getColumnIndex("title")),
                    content = it.getString(it.getColumnIndex("content")),
                    date = it.getString(it.getColumnIndex("date"))
                )
                tasks.add(task)
            }
        }
    }
}

