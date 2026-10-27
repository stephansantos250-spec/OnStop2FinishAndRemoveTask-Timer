package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import rikka.shizuku.Shizuku

@Composable
fun ShizukuSetupScreen(onGranted: () -> Unit) {
    DisposableEffect(onGranted) {
        val listener = Shizuku.OnRequestPermissionResultListener { _, result ->
            if (result == PackageManager.PERMISSION_GRANTED) onGranted()
        }
        Shizuku.addRequestPermissionResultListener(listener)
        onDispose { Shizuku.removeRequestPermissionResultListener(listener) }
    }
    GlassShizukuContent(permission = true) { Shizuku.requestPermission(2525) }
}
