package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.takusan23.onstop2finishandremovetask.R
import io.github.takusan23.onstop2finishandremovetask.ui.component.*

@Composable
fun ShizukuPrepareScreen() {
    val context = LocalContext.current
    GlassShizukuContent(permission = false) {
        context.startActivity(Intent(Intent.ACTION_VIEW, "https://shizuku.rikka.app/".toUri()))
    }
}

@Composable
internal fun GlassShizukuContent(permission: Boolean, onAction: () -> Unit = {}) {
    GlassBackdrop {
        Scaffold(containerColor = Color.Transparent, topBar = {
            GlassTopBar("Shizuku", stringResource(R.string.glass_connect_subtitle))
        }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    GlassPanel {
                        TimerMark(Modifier.size(64.dp))
                        Text(stringResource(if (permission) R.string.glass_permission_title else R.string.glass_connect_title), style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(if (permission) R.string.screen_shizuku_setup_required_message_2 else R.string.screen_shizuku_prepare_description_body),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = onAction, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(if (permission) R.string.screen_shizuku_setup_request_permission_button else R.string.screen_shizuku_prepare_button))
                        }
                    }
                }
                item {
                    GlassPanel {
                        Text(stringResource(R.string.glass_connect_steps_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.glass_connect_steps), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
