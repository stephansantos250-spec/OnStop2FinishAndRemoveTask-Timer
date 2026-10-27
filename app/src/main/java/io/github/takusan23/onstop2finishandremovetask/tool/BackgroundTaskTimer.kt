package io.github.takusan23.onstop2finishandremovetask.tool

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Call all methods on the same dispatcher (the service uses Dispatchers.Main). */
class BackgroundTaskTimer(
    private val scope: CoroutineScope,
    private val onExpired: (Task) -> Unit
) {
    data class Task(val id: Int, val packageName: String, val label: String)
    private data class Pending(val task: Task, val job: Job)

    private val pending = mutableMapOf<Int, Pending>()
    private var registered = emptySet<String>()
    private var delayMillis = 60_000L
    private var foreground: Task? = null

    fun configure(packages: Set<String>, minutes: Int) {
        registered = packages
        pending.values.filter { it.task.packageName !in packages }
            .map { it.task.id }.forEach(::onTaskRemoved)
        val newDelay = minutes.coerceIn(1, 5) * 60_000L
        if (newDelay != delayMillis) {
            val tasks = pending.values.map { it.task }
            cancelAll()
            delayMillis = newDelay
            // A settings change restarts pending timers with the new duration.
            tasks.forEach(::schedule)
        }
    }

    fun onForeground(task: Task) {
        val previous = foreground
        foreground = task
        pending.values.filter { it.task.packageName == task.packageName }
            .map { it.task.id }.forEach(::cancelPending)
        if (previous != null && previous.packageName != task.packageName) {
            schedule(previous)
        }
    }

    fun onTaskRemoved(taskId: Int) {
        cancelPending(taskId)
        if (foreground?.id == taskId) foreground = null
    }

    fun cancelAll() {
        pending.values.forEach { it.job.cancel() }
        pending.clear()
    }

    private fun cancelPending(taskId: Int) {
        pending.remove(taskId)?.job?.cancel()
    }

    private fun schedule(task: Task) {
        if (task.packageName !in registered || foreground?.packageName == task.packageName) return
        cancelPending(task.id)
        val job = scope.launch {
            delay(delayMillis)
            pending.remove(task.id)
            if (task.packageName in registered && foreground?.packageName != task.packageName) {
                onExpired(task)
            }
        }
        pending[task.id] = Pending(task, job)
    }
}
