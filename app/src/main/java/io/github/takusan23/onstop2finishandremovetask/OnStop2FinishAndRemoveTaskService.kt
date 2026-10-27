package io.github.takusan23.onstop2finishandremovetask

import android.app.ActivityManager
import android.app.ITaskStackListener
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import android.window.TaskSnapshot
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.takusan23.onstop2finishandremovetask.tool.BackgroundTaskTimer
import io.github.takusan23.onstop2finishandremovetask.tool.CloseDelaySettings
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import io.github.takusan23.onstop2finishandremovetask.tool.ShizukuServiceTool
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** 今表示されている Activity が離れた（ほかアプリに切り替えた）時に Activity を終了する */
class OnStop2FinishAndRemoveTaskService : Service() {

    private val stopAction get() = "$packageName.action_service_stop"

    private val scope = MainScope()
    private val taskTimer = BackgroundTaskTimer(scope) { task ->
        try {
            if (ShizukuServiceTool.activity.removeTask(task.id)) {
                Toast.makeText(this, getString(
                    R.string.service_onstop_2_finish_and_remove_task_task_removed_toast_message_format,
                    task.label
                ), Toast.LENGTH_SHORT).show()
            }
        } catch (error: RemoteException) {
            Log.e("BackgroundTaskTimer", "Shizuku connection lost", error)
            stopSelf()
        } catch (error: SecurityException) {
            Log.e("BackgroundTaskTimer", "Shizuku permission revoked", error)
            stopSelf()
        }
    }

    private sealed interface TaskEvent {
        data class Foreground(val task: BackgroundTaskTimer.Task) : TaskEvent
        data class Removed(val taskId: Int) : TaskEvent
    }

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == stopAction) {
                stopSelf()
            }
        }
    }

    /** Buffered events avoid dropping quick switches between registered apps. */
    private val taskEvents = callbackFlow<TaskEvent> {
        val listener = object : ITaskStackListener.Stub() {
            override fun onTaskStackChanged() {
                // do nothing
            }

            override fun onActivityPinned(packageName: String?, userId: Int, taskId: Int, stackId: Int) {
                // do nothing
            }

            override fun onActivityUnpinned() {
                // do nothing
            }

            override fun onActivityRestartAttempt(task: ActivityManager.RunningTaskInfo?, homeTaskVisible: Boolean, clearedTask: Boolean, wasVisible: Boolean) {
                // do nothing
            }

            override fun onActivityForcedResizable(packageName: String?, taskId: Int, reason: Int) {
                // do nothing
            }

            override fun onActivityDismissingDockedTask() {
                // do nothing
            }

            override fun onActivityLaunchOnSecondaryDisplayFailed(taskInfo: ActivityManager.RunningTaskInfo?, requestedDisplayId: Int) {
                // do nothing
            }

            override fun onActivityLaunchOnSecondaryDisplayRerouted(taskInfo: ActivityManager.RunningTaskInfo?, requestedDisplayId: Int) {
                // do nothing
            }

            override fun onTaskCreated(taskId: Int, componentName: ComponentName?) {
                // do nothing
            }

            override fun onTaskRemoved(taskId: Int) {
                trySend(TaskEvent.Removed(taskId))
            }

            override fun onTaskMovedToFront(taskInfo: ActivityManager.RunningTaskInfo?) {
                val packageName = taskInfo?.topActivity?.packageName ?: return
                trySend(TaskEvent.Foreground(BackgroundTaskTimer.Task(
                    id = taskInfo.taskId,
                    packageName = packageName,
                    label = taskInfo.topActivityInfo?.loadLabel(packageManager)?.toString() ?: packageName
                )))
            }

            override fun onTaskDescriptionChanged(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onActivityRequestedOrientationChanged(taskId: Int, requestedOrientation: Int) {
                // do nothing
            }

            override fun onTaskRemovalStarted(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onTaskProfileLocked(taskInfo: ActivityManager.RunningTaskInfo?, userId: Int) {
                // do nothing
            }

            override fun onTaskSnapshotChanged(taskId: Int, snapshot: TaskSnapshot?) {
                // do nothing
            }

            override fun onTaskSnapshotInvalidated(taskId: Int) {
                // do nothing
            }

            override fun onBackPressedOnTaskRoot(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onTaskDisplayChanged(taskId: Int, newDisplayId: Int) {
                // do nothing
            }

            override fun onRecentTaskListUpdated() {
                // do nothing
            }

            override fun onRecentTaskListFrozenChanged(frozen: Boolean) {
                // do nothing
            }

            override fun onRecentTaskRemovedForAddTask(taskId: Int) {
                // do nothing
            }

            override fun onTaskFocusChanged(taskId: Int, focused: Boolean) {
                // do nothing
            }

            override fun onTaskRequestedOrientationChanged(taskId: Int, requestedOrientation: Int) {
                // do nothing
            }

            override fun onActivityRotation(displayId: Int) {
                // do nothing
            }

            override fun onTaskMovedToBack(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onLockTaskModeChanged(mode: Int) {
                // do nothing
            }

            override fun onActivityDismissingSplitTask(p0: String?) {
                // do nothing
            }

            override fun onTaskbarIconVisibleChangeRequest(p0: ComponentName?, p1: Boolean) {
                // do nothing
            }

            override fun onTaskRecentsAnimationAdded(p0: Int) {
                // do nothing
            }

            override fun onTaskRecentsAnimationRemoved(p0: Int) {
                // do nothing
            }

            override fun onOccludeChangeNotice(p0: ComponentName?, p1: Boolean) {
                // do nothing
            }

            override fun onRecentTaskRemoved(p0: Int) {
                // do nothing
            }

            override fun onTaskWindowingModeChanged(p0: Int) {
                // do nothing
            }
        }

        ShizukuServiceTool.activity.registerTaskStackListener(listener)
        awaitClose { ShizukuServiceTool.activity.unregisterTaskStackListener(listener) }
    }.buffer(Channel.UNLIMITED)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // サービスを止められるように BroadcastReceiver
        // 通知のボタンから発行されるので exported ?
        val intentFilter = IntentFilter().apply {
            addAction(stopAction)
        }
        ContextCompat.registerReceiver(this, broadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)

        scope.launch {
            combine(
                RegisterAppListTool.realtimeReadApplicationIdList(this@OnStop2FinishAndRemoveTaskService),
                CloseDelaySettings.observe(this@OnStop2FinishAndRemoveTaskService)
            ) { packages, minutes -> packages.toSet() to minutes }
                .collect { (packages, minutes) -> taskTimer.configure(packages, minutes) }
        }
        scope.launch {
            try {
                taskEvents.collect { event ->
                    when (event) {
                        is TaskEvent.Foreground -> taskTimer.onForeground(event.task)
                        is TaskEvent.Removed -> taskTimer.onTaskRemoved(event.taskId)
                    }
                }
            } catch (error: RemoteException) {
                Log.e("BackgroundTaskTimer", "Cannot observe tasks", error)
                stopSelf()
            } catch (error: SecurityException) {
                Log.e("BackgroundTaskTimer", "Cannot observe tasks", error)
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        taskTimer.cancelAll()
        scope.cancel()
        unregisterReceiver(broadcastReceiver)
    }

    private fun startForegroundService() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_LOW).apply {
                setName(getString(R.string.service_onstop_2_finish_and_remove_task_notification_channel_name))
            }.build()
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(getString(R.string.service_onstop_2_finish_and_remove_task_running_notification_title))
            setContentText(getString(R.string.service_onstop_2_finish_and_remove_task_running_notification_text))
            setSmallIcon(R.drawable.onstop2finishandremovetask)
            // 終了ボタンを追加
            val stopPendingIntent = PendingIntent.getBroadcast(this@OnStop2FinishAndRemoveTaskService, 1, Intent(stopAction), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            addAction(NotificationCompat.Action(R.drawable.onstop2finishandremovetask, getString(R.string.service_onstop_2_finish_and_remove_task_running_notification_action_stop), stopPendingIntent))
        }.build()
        startForeground(1, notification)
    }

    companion object {
        private const val CHANNEL_ID = "running_notification_channel_id"

        fun stop(context: Context) {
            context.stopService(Intent(context, OnStop2FinishAndRemoveTaskService::class.java))
        }

        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, OnStop2FinishAndRemoveTaskService::class.java))
        }
    }
}