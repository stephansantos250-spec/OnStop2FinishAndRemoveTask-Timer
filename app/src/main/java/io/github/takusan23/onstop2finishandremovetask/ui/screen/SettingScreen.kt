package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import io.github.takusan23.onstop2finishandremovetask.OnStop2FinishAndRemoveTaskService
import io.github.takusan23.onstop2finishandremovetask.R
import io.github.takusan23.onstop2finishandremovetask.tool.CloseDelaySettings
import io.github.takusan23.onstop2finishandremovetask.ui.component.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SettingScreen(onNavigation: (RoutePaths) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val seconds by remember(context) { CloseDelaySettings.observe(context) }.collectAsState(initial = CloseDelaySettings.DEFAULT_SECONDS)
    GlassSettingsContent(seconds, onSeconds = { selected -> scope.launch { CloseDelaySettings.save(context, selected) } },
        onBack = onBack, onStop = { OnStop2FinishAndRemoveTaskService.stop(context) },
        onLicense = { onNavigation(RoutePaths.License) },
        onGitHub = { context.startActivity(Intent(Intent.ACTION_VIEW,
            "https://github.com/stephansantos250-spec/OnStop2FinishAndRemoveTask-Timer".toUri())) })
}

@Composable
internal fun GlassSettingsContent(seconds: Int, onSeconds: (Int) -> Unit = {}, onBack: () -> Unit = {},
    onStop: () -> Unit = {}, onLicense: () -> Unit = {}, onGitHub: () -> Unit = {}) {
    var draft by remember(seconds) { mutableFloatStateOf(seconds.toFloat()) }
    val arc by animateFloatAsState(draft / 60f, tween(220), label = "timerArc")
    val primary = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.outlineVariant
    GlassBackdrop {
        Scaffold(containerColor = Color.Transparent, topBar = {
            GlassTopBar(stringResource(R.string.screen_setting_title), onBack = onBack)
        }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    GlassPanel {
                        Text(stringResource(R.string.glass_settings_title), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.glass_settings_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Box(Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.size(148.dp)) {
                                val inset = 7.dp.toPx()
                                val ringSize = Size(size.width - inset * 2, size.height - inset * 2)
                                drawArc(track, 135f, 270f, false, Offset(inset, inset), ringSize, style = Stroke(7.dp.toPx(), cap = StrokeCap.Round))
                                drawArc(primary, 135f, 270f * arc, false, Offset(inset, inset), ringSize, style = Stroke(7.dp.toPx(), cap = StrokeCap.Round))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedContent(draft.roundToInt(), label = "timerNumber") {
                                    Text(it.toString(), fontSize = 52.sp, fontWeight = FontWeight.Bold, color = primary)
                                }
                                Text(stringResource(R.string.glass_seconds_unit), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text(stringResource(R.string.glass_seconds_hint), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(modifier = Modifier.testTag("delay_slider"), value = draft,
                            onValueChange = { draft = it.roundToInt().toFloat() }, valueRange = 20f..60f,
                            onValueChangeFinished = { onSeconds(draft.roundToInt().coerceIn(20, 60)) })
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(20, 30, 40, 50, 60).forEach { value ->
                                FilterChip(selected = draft.roundToInt() == value,
                                    onClick = { draft = value.toFloat(); onSeconds(value) },
                                    label = { Text(value.toString(), maxLines = 1, softWrap = false) })
                            }
                        }
                        Text(stringResource(R.string.glass_timer_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item { GlassAction(stringResource(R.string.screen_setting_github_title), stringResource(R.string.glass_github_body), onGitHub) }
                item { GlassAction(stringResource(R.string.screen_setting_license_title), stringResource(R.string.glass_license_body), onLicense) }
                item { GlassAction(stringResource(R.string.screen_setting_stop_service), stringResource(R.string.glass_stop_body), onStop, true) }
            }
        }
    }
}

@Composable
private fun GlassAction(title: String, body: String, onClick: () -> Unit, destructive: Boolean = false) {
    GlassPanel(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(painterResource(R.drawable.chevron_24px), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
