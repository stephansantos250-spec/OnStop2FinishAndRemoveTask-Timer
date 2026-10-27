package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import io.github.takusan23.onstop2finishandremovetask.OnStop2FinishAndRemoveTaskService
import io.github.takusan23.onstop2finishandremovetask.R
import io.github.takusan23.onstop2finishandremovetask.tool.AppTypeFilter
import io.github.takusan23.onstop2finishandremovetask.ui.component.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppListScreen(viewModel: AppListViewModel, onNavigate: (RoutePaths) -> Unit) {
    val appList by viewModel.appListFlow.collectAsState()
    val option by viewModel.searchOptionFlow.collectAsState()
    val count by viewModel.selectedCountFlow.collectAsState(0)
    val minutes by viewModel.closeDelayFlow.collectAsState(1)
    val loading by viewModel.loadingFlow.collectAsState()
    var search by rememberSaveable { mutableStateOf(false) }
    if (search) SearchBottomSheet({ search = false }, option, viewModel::updateSearchOption)
    GlassAppListContent(appList, option, count, minutes, loading,
        onOption = viewModel::updateSearchOption, onCheck = viewModel::registerApp,
        onSearch = { search = true }, onSettings = { onNavigate(RoutePaths.Setting) },
        notification = { NotificationPermissionCard() })
}

@Composable
internal fun GlassAppListContent(
    apps: List<AppListViewModel.AppInfo>, option: AppListViewModel.SearchOption,
    selectedCount: Int, minutes: Int, loading: Boolean = false,
    onOption: (AppListViewModel.SearchOption) -> Unit = {},
    onCheck: (Boolean, String) -> Unit = { _, _ -> },
    onSearch: () -> Unit = {}, onSettings: () -> Unit = {},
    loadIcons: Boolean = true, notification: @Composable () -> Unit = {}
) {
    GlassBackdrop {
        Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent, topBar = {
            GlassTopBar("Timer", stringResource(R.string.glass_app_subtitle)) {
                IconButton(onClick = onSearch) { Icon(painterResource(R.drawable.search_24px), stringResource(R.string.screen_app_list_search_bottomsheet_title)) }
                IconButton(onClick = onSettings) { Icon(painterResource(R.drawable.settings_24px), stringResource(R.string.screen_setting_title)) }
            }
        }) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item(key = "summary") {
                    GlassPanel {
                        Text(stringResource(R.string.glass_hero_title), style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(R.string.glass_hero_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                AnimatedContent(selectedCount, label = "selectedCount") { Text(it.toString(), style = MaterialTheme.typography.headlineLarge) }
                                Text(stringResource(R.string.glass_selected), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(Modifier.weight(1f)) {
                                AnimatedContent(minutes, label = "summaryMinutes") { Text("$it min", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) }
                                Text(stringResource(R.string.glass_delay_label), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        TextButton(onClick = onSettings, contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)) {
                            Text(stringResource(R.string.glass_adjust_timer))
                        }
                    }
                }
                item(key = "notification") { notification() }
                item(key = "filters") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.screen_app_list_type_title), style = MaterialTheme.typography.titleSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppTypeFilter.entries.forEach { type ->
                                val label = when (type) {
                                    AppTypeFilter.ALL -> R.string.screen_app_list_filter_all
                                    AppTypeFilter.INSTALLED -> R.string.screen_app_list_filter_installed
                                    AppTypeFilter.SYSTEM -> R.string.screen_app_list_filter_system
                                }
                                FilterChip(selected = option.appType == type,
                                    onClick = { onOption(option.copy(appType = type)) },
                                    label = { Text(stringResource(label)) })
                            }
                        }
                        if (option.appType != AppTypeFilter.INSTALLED) Text(
                            stringResource(R.string.screen_app_list_system_warning),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
                if (loading) item(key = "loading") {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text(stringResource(R.string.glass_loading), Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodySmall)
                }
                if (!loading && apps.isEmpty()) item(key = "empty") {
                    GlassPanel {
                        Text(stringResource(R.string.glass_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.glass_empty_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(apps, key = { it.packageName }) { app ->
                    GlassAppRow(app, onCheck, loadIcons, Modifier.animateItem(
                        fadeInSpec = tween(180), placementSpec = tween(220), fadeOutSpec = tween(120)))
                }
            }
        }
    }
}

@Composable
private fun GlassAppRow(info: AppListViewModel.AppInfo, onCheck: (Boolean, String) -> Unit, loadIcons: Boolean, modifier: Modifier) {
    val context = LocalContext.current
    val pixels = with(LocalDensity.current) { 44.dp.roundToPx() }
    val bitmap by produceState<ImageBitmap?>(null, info.packageName, loadIcons, pixels) {
        if (loadIcons) value = withContext(Dispatchers.IO) {
            try { context.packageManager.getApplicationIcon(info.packageName).toBitmap(pixels, pixels).asImageBitmap() }
            catch (_: PackageManager.NameNotFoundException) { null }
        }
    }
    GlassPanel(modifier, highlighted = info.isRegistered) {
        Row(Modifier.fillMaxWidth().toggleable(info.isRegistered, role = Role.Switch,
            onValueChange = { onCheck(it, info.packageName) }),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                if (bitmap != null) Image(bitmap!!, null, Modifier.size(36.dp))
                else Text(info.appName.take(1).uppercase(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(info.appName, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(stringResource(if (info.isSystemApp) R.string.screen_app_list_type_system else R.string.screen_app_list_type_installed),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AnimatedVisibility(info.isRegistered,
                    enter = fadeIn(tween(160)) + expandVertically(tween(180)),
                    exit = fadeOut(tween(120)) + shrinkVertically(tween(160))) {
                    Text(info.packageName, style = MaterialTheme.typography.bodySmall,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Switch(info.isRegistered, onCheckedChange = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBottomSheet(
    onClose: () -> Unit,
    searchOption: AppListViewModel.SearchOption,
    onUpdate: (AppListViewModel.SearchOption) -> Unit
) {

    fun update(
        searchAppName: String = searchOption.searchAppName,
        searchPackageName: String = searchOption.searchPackageName,
        isOnlyRegisterApp: Boolean = searchOption.isOnlyRegisterApp
    ) {
        onUpdate(
            searchOption.copy(
                searchAppName = searchAppName,
                searchPackageName = searchPackageName,
                isOnlyRegisterApp = isOnlyRegisterApp
            )
        )
    }

    @Composable
    fun SwitchWithLabel(
        modifier: Modifier = Modifier,
        label: String,
        value: Boolean,
        onChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = modifier
                .toggleable(value = value, onValueChange = onChange)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = value,
                onCheckedChange = null
            )
        }
    }

    ModalBottomSheet(onDismissRequest = onClose, containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.imePadding().verticalScroll(rememberScrollState()).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.screen_app_list_search_bottomsheet_title),
                fontSize = 20.sp
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = searchOption.searchAppName,
                onValueChange = { update(searchAppName = it) },
                label = { Text(stringResource(R.string.screen_app_list_search_bottomsheet_search_app_name)) }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = searchOption.searchPackageName,
                onValueChange = { update(searchPackageName = it) },
                label = { Text(stringResource(R.string.screen_app_list_search_bottomsheet_search_package_name)) }
            )
            SwitchWithLabel(
                label = stringResource(R.string.screen_app_list_search_bottomsheet_only_register_app),
                value = searchOption.isOnlyRegisterApp,
                onChange = { update(isOnlyRegisterApp = it) }
            )
        }
    }
}

@Composable
private fun NotificationPermissionCard(modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val isGranted = remember { mutableStateOf(ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) }
    val permissionRequester = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            // 再起動しておく...
            isGranted.value = true
            OnStop2FinishAndRemoveTaskService.stop(context)
            OnStop2FinishAndRemoveTaskService.start(context)
        }
    }

    if (isGranted.value) return

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(stringResource(R.string.screen_app_list_permission_card_title))
            Button(onClick = { permissionRequester.launch(android.Manifest.permission.POST_NOTIFICATIONS) }) {
                Text(stringResource(R.string.screen_app_list_permission_card_button))
            }
        }
    }
}