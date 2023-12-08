package simulasi.aad.todoapp.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import simulasi.aad.todoapp.data.Task
import simulasi.aad.todoapp.data.TaskRepository
import simulasi.aad.todoapp.ui.detail.DetailTaskActivity
import simulasi.aad.todoapp.utils.DateConverter
import simulasi.aad.todoapp.utils.NOTIFICATION_CHANNEL_ID
import simulasi.aad.todoapp.utils.TASK_ID
import simulasi.aad.todoapp.R

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent (Done)
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val notificationPref =
            pref.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)
        if (notificationPref) {
            val nearestTask =
                TaskRepository.getInstance(context = applicationContext).getNearestActiveTask()
            getPendingIntent(nearestTask)?.let {
                showNotification(
                    applicationContext,
                    task = nearestTask,
                    pendingIntent = it
                )
            }
        }
        return Result.success()
    }

    private fun showNotification(context: Context, task: Task, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "Task Reminder"

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val nBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(task.title)
            .setContentIntent(pendingIntent)
            .setContentText(
                context.getString(
                    R.string.notify_content, DateConverter
                        .convertMillisToString(
                            task.dueDateMillis
                        )
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, nBuilder.build())
        }
    }

}
