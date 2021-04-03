package com.draco.cachie.fragments

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.preference.*
import com.draco.cachie.R
import com.draco.cachie.repositories.constants.SettingsConstants
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar

class MainPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var reset: Preference
    private lateinit var sysStorageThresholdPercentage: EditTextPreference
    private lateinit var sysStorageThresholdMaxBytes: EditTextPreference
    private lateinit var sysStorageFullThresholdBytes: EditTextPreference
    private lateinit var sysStorageCachePercentage: EditTextPreference
    private lateinit var sysStorageCacheMaxBytes: EditTextPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reset = findPreference(getString(R.string.pref_action_key_reset))!!
        sysStorageThresholdPercentage = findPreference(getString(R.string.pref_config_key_sys_storage_threshold_percentage))!!
        sysStorageThresholdMaxBytes = findPreference(getString(R.string.pref_config_key_sys_storage_threshold_max_bytes))!!
        sysStorageFullThresholdBytes = findPreference(getString(R.string.pref_config_key_sys_storage_full_threshold_bytes))!!
        sysStorageCachePercentage = findPreference(getString(R.string.pref_config_key_sys_storage_cache_percentage))!!
        sysStorageCacheMaxBytes = findPreference(getString(R.string.pref_config_key_sys_storage_cache_max_bytes))!!

        refreshSettings()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.pref_action_key_reset) -> resetSettings()

            getString(R.string.pref_developer_key) -> openURL(getString(R.string.developer_url))
            getString(R.string.pref_source_key) -> openURL(getString(R.string.source_url))
            getString(R.string.pref_contact_key) -> openURL(getString(R.string.contact_url))
            getString(R.string.pref_licenses_key) -> {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onPreferenceTreeClick(preference)
        }
        return true
    }

    /**
     * Put everything back to defaults
     */
    private fun resetSettings() {
        val contentResolver = requireContext().contentResolver
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_PERCENTAGE, null)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_MAX_BYTES, null)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_FULL_THRESHOLD_BYTES, null)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_PERCENTAGE, null)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_MAX_BYTES, null)
        refreshSettings()
    }

    /**
     * Update the UI to show the new constants
     */
    private fun refreshSettings() {
        val contentResolver = requireContext().contentResolver
        sysStorageThresholdPercentage.text = Settings.Global.getString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_PERCENTAGE)
        sysStorageThresholdMaxBytes.text = Settings.Global.getString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_MAX_BYTES)
        sysStorageFullThresholdBytes.text = Settings.Global.getString(contentResolver, SettingsConstants.SYS_STORAGE_FULL_THRESHOLD_BYTES)
        sysStorageCachePercentage.text = Settings.Global.getString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_PERCENTAGE)
        sysStorageCacheMaxBytes.text = Settings.Global.getString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_MAX_BYTES)
    }

    /**
     * Take the UI settings and apply them as constants
     */
    private fun applySettings() {
        val contentResolver = requireContext().contentResolver
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_PERCENTAGE, sysStorageThresholdPercentage.text)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_THRESHOLD_MAX_BYTES, sysStorageThresholdMaxBytes.text)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_FULL_THRESHOLD_BYTES, sysStorageFullThresholdBytes.text)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_PERCENTAGE, sysStorageCachePercentage.text)
        Settings.Global.putString(contentResolver, SettingsConstants.SYS_STORAGE_CACHE_MAX_BYTES, sysStorageCacheMaxBytes.text)
    }

    /**
     * Open a URL for the user
     */
    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * When settings are changed, apply the new config
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        applySettings()
    }
}