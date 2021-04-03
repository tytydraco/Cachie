package com.draco.cachie.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.draco.cachie.BuildConfig
import com.draco.cachie.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PermissionActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _permissionGranted = MutableLiveData(false)
    val permissionGranted: LiveData<Boolean> = _permissionGranted

    private fun askRootPermission() {
        try {
            ProcessBuilder(
                "su",
                "-c",
                "pm grant ${BuildConfig.APPLICATION_ID} android.permission.WRITE_SECURE_SETTINGS"
            ).start()
        } catch (_: Exception) {}
    }

    private fun isWriteSecureSettingsPermissionGranted(): Boolean {
        val context = getApplication<Application>().applicationContext
        return PermissionUtils.isPermissionsGranted(context, android.Manifest.permission.WRITE_SECURE_SETTINGS)
    }

    private fun startPermissionCheckLoop() {
        viewModelScope.launch(Dispatchers.IO) {
            while (!isWriteSecureSettingsPermissionGranted())
                delay(100)
            _permissionGranted.postValue(true)
        }
    }

    init {
        if (!isWriteSecureSettingsPermissionGranted()) {
            viewModelScope.launch(Dispatchers.IO) {
                askRootPermission()
            }
            startPermissionCheckLoop()
        }
    }
}