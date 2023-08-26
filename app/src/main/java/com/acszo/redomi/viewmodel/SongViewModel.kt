package com.acszo.redomi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acszo.redomi.model.AppDetails
import com.acszo.redomi.model.Platform
import com.acszo.redomi.model.Providers
import com.acszo.redomi.model.SongInfo
import com.acszo.redomi.repository.DataStoreRepository
import com.acszo.redomi.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

enum class AppList {
    INSTALLED,
    ALL
}

@HiltViewModel
class SongViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _songInfo: MutableStateFlow<SongInfo?> = MutableStateFlow(null)
    val songInfo: StateFlow<SongInfo?> = _songInfo.asStateFlow()

    private val _platforms: MutableStateFlow<List<AppDetails>> = MutableStateFlow(emptyList())
    val platforms: StateFlow<List<AppDetails>> = _platforms.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getPlatforms(url: String, appList: AppList) = viewModelScope.launch {
        try {
            _isLoading.update { true }

            val response: Providers = SongRepository().getSongs(url)
            _songInfo.update { response.entitiesByUniqueId[response.entitiesByUniqueId.keys.first().toString()] }

            val apps = Platform.platforms.filter {
                if (appList == AppList.INSTALLED) {
                    val installedAppsDataStore = dataStoreRepository.readInstalledApps()
                    installedAppsDataStore.contains(it)
                } else {
                    val allAppsDataStore = dataStoreRepository.readAllApps()
                    allAppsDataStore.contains(it)
                }
            }

            apps.forEach {
                it.link = response.linksByPlatform[it.title]?.url ?: ""
            }
            _platforms.update {
                apps.filter {
                    it.title in response.linksByPlatform.keys
                }
            }

            _isLoading.update { false }
        } catch (e: Exception) {
            print(e.message)
        }
    }

}