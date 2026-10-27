package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.takusan23.onstop2finishandremovetask.OnStop2FinishAndRemoveTaskService
import io.github.takusan23.onstop2finishandremovetask.R
import io.github.takusan23.onstop2finishandremovetask.tool.CloseDelaySettings
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val GitHubUrl = "https://github.com/takusan23/OnStop2FinishAndRemoveTask"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onNavigation: (RoutePaths) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val minutes by remember(context) { CloseDelaySettings.observe(context) }
        .collectAsState(initial = CloseDelaySettings.DEFAULT_MINUTES)
    var sliderMinutes by remember(minutes) { mutableFloatStateOf(minutes.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.screen_setting_title)) })
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(stringResource(R.string.screen_setting_close_delay_title))
                    Text(stringResource(R.string.screen_setting_close_delay_value, sliderMinutes.roundToInt()))
                    Slider(
                        value = sliderMinutes,
                        onValueChange = { sliderMinutes = it },
                        onValueChangeFinished = {
                            val selected = sliderMinutes.roundToInt().coerceIn(1, 5)
                            scope.launch { CloseDelaySettings.save(context, selected) }
                        },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text(stringResource(R.string.screen_setting_close_delay_description))
                }
            }
            item {
                SettingItem(
                    title = stringResource(R.string.screen_setting_stop_service),
                    onClick = { OnStop2FinishAndRemoveTaskService.stop(context) }
                )
            }
            item {
                SettingItem(
                    title = stringResource(R.string.screen_setting_license_title),
                    onClick = { onNavigation(RoutePaths.License) }
                )
            }
            item {
                SettingItem(
                    title = stringResource(R.string.screen_setting_github_title),
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, GitHubUrl.toUri())) }
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(title)
    }
}