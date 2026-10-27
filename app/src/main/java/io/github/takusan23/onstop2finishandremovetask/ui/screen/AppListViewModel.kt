package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.onstop2finishandremovetask.tool.AppTypeFilter
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import io.github.takusan23.onstop2finishandremovetask.tool.CloseDelaySettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppListViewModel(private val application: Application) : AndroidViewModel(application) {
    private val context get() = application.applicationContext
    private val _searchOptionFlow = MutableStateFlow(SearchOption())

    private val _loadingFlow = MutableStateFlow(true)
    val loadingFlow = _loadingFlow.asStateFlow()
    val closeDelayFlow = CloseDelaySettings.observe(context)
    val selectedCountFlow = RegisterAppListTool.realtimeReadApplicationIdList(context)
        .map { it.distinct().size }.distinctUntilChanged()

    private val installedApps = flow {
        emit(withContext(Dispatchers.IO) {
            context.packageManager.getInstalledPackages(0).mapNotNull { info ->
                val applicationInfo = info.applicationInfo ?: return@mapNotNull null
                AppInfo(false,
                    (applicationInfo.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0,
                    applicationInfo.loadLabel(context.packageManager).toString(), info.packageName, info.versionName)
            }.sortedBy { it.appName.lowercase() }
        })
        _loadingFlow.value = false
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val appListFlow = combine(installedApps,
        RegisterAppListTool.realtimeReadApplicationIdList(context), _searchOptionFlow) { apps, registered, option ->
        val registeredSet = registered.toSet()
        apps.asSequence().map { it.copy(isRegistered = it.packageName in registeredSet) }
            .filter { option.appType.matches(it.isSystemApp) &&
                it.appName.contains(option.searchAppName, ignoreCase = true) &&
                it.packageName.contains(option.searchPackageName, ignoreCase = true) &&
                (!option.isOnlyRegisterApp || it.isRegistered) }.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * アプリの検索
     */
    val searchOptionFlow = _searchOptionFlow.asStateFlow()

    /**
     * アプリを登録する
     *
     * @param isRegistered 登録するなら true
     * @param packageName パッケージ名
     */
    fun registerApp(isRegistered: Boolean, packageName: String) {
        viewModelScope.launch {
            RegisterAppListTool.setApplicationRegistered(context, packageName, isRegistered)
        }
    }

    /**
     * 検索を変更する
     */
    fun updateSearchOption(searchOption: SearchOption) {
        _searchOptionFlow.value = searchOption
    }

    data class AppInfo(
        val isRegistered: Boolean,
        val isSystemApp: Boolean,
        val appName: String,
        val packageName: String,
        val versionName: String?
    )

    data class SearchOption(
        val searchAppName: String = "",
        val searchPackageName: String = "",
        val appType: AppTypeFilter = AppTypeFilter.ALL,
        val isOnlyRegisterApp: Boolean = false
    ) {
        companion object {
            val EMPTY = SearchOption()
        }
    }
}
