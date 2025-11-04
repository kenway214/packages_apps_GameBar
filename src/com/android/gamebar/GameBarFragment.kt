/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */


package com.android.gamebar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.android.gamebar.utils.PartsCustomSeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.android.settingslib.widget.MainSwitchPreference
import com.android.gamebar.R
import com.android.gamebar.colorpicker.ColorPreferenceCompat

import java.util.Locale

import com.android.settingslib.widget.SettingsBasePreferenceFragment

class GameBarFragment : SettingsBasePreferenceFragment() {

    private var gameBar: GameBar? = null
    private var masterSwitch: MainSwitchPreference? = null
    private var autoEnableSwitch: SwitchPreferenceCompat? = null
    
    private val importPresetLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { importPresetFromUri(it) }
    }
    
    private val presetManagementLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Preset was loaded, refresh UI
            refreshPreferences()
        }
    }
    private var fpsSwitch: SwitchPreferenceCompat? = null
    private var fpsDisplayModePref: ListPreference? = null
    private var frameTimeSwitch: SwitchPreferenceCompat? = null
    private var batteryTempSwitch: SwitchPreferenceCompat? = null
    private var cpuUsageSwitch: SwitchPreferenceCompat? = null
    private var cpuClockSwitch: SwitchPreferenceCompat? = null
    private var cpuTempSwitch: SwitchPreferenceCompat? = null
    private var ramSwitch: SwitchPreferenceCompat? = null
    private var gpuUsageSwitch: SwitchPreferenceCompat? = null
    private var gpuClockSwitch: SwitchPreferenceCompat? = null
    private var gpuTempSwitch: SwitchPreferenceCompat? = null
    private var singleTapEnablePref: SwitchPreferenceCompat? = null
    private var singleTapFunctionPref: ListPreference? = null
    private var doubleTapEnablePref: SwitchPreferenceCompat? = null
    private var doubleTapFunctionPref: ListPreference? = null
    private var longPressEnablePref: SwitchPreferenceCompat? = null
    private var longPressFunctionPref: ListPreference? = null
    private var longPressTimeoutPref: ListPreference? = null
    private var textSizePref: PartsCustomSeekBarPreference? = null
    private var bgAlphaPref: PartsCustomSeekBarPreference? = null
    private var cornerRadiusPref: PartsCustomSeekBarPreference? = null
    private var paddingPref: PartsCustomSeekBarPreference? = null
    private var itemSpacingPref: PartsCustomSeekBarPreference? = null
    private var updateIntervalPref: ListPreference? = null
    private var textColorPref: ListPreference? = null
    private var titleColorPref: ColorPreferenceCompat? = null
    private var valueColorPref: ColorPreferenceCompat? = null
    private var backgroundColorPref: ColorPreferenceCompat? = null
    private var positionPref: ListPreference? = null
    private var splitModePref: ListPreference? = null
    private var overlayFormatPref: ListPreference? = null
    private var ramSpeedSwitch: SwitchPreferenceCompat? = null
    private var ramTempSwitch: SwitchPreferenceCompat? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.game_bar_preferences, rootKey)

        gameBar = GameBar.getInstance(requireContext())

        // Initialize all preferences
        masterSwitch = findPreference("game_bar_enable")
        autoEnableSwitch = findPreference("game_bar_auto_enable")
        fpsSwitch = findPreference("game_bar_fps_enable")
        fpsDisplayModePref = findPreference("game_bar_fps_display_mode")
        frameTimeSwitch = findPreference("game_bar_frame_time_enable")
        batteryTempSwitch = findPreference("game_bar_temp_enable")
        cpuUsageSwitch = findPreference("game_bar_cpu_usage_enable")
        cpuClockSwitch = findPreference("game_bar_cpu_clock_enable")
        cpuTempSwitch = findPreference("game_bar_cpu_temp_enable")
        ramSwitch = findPreference("game_bar_ram_enable")
        gpuUsageSwitch = findPreference("game_bar_gpu_usage_enable")
        gpuClockSwitch = findPreference("game_bar_gpu_clock_enable")
        gpuTempSwitch = findPreference("game_bar_gpu_temp_enable")
        ramSpeedSwitch = findPreference("game_bar_ram_speed_enable")
        ramTempSwitch = findPreference("game_bar_ram_temp_enable")

        singleTapEnablePref = findPreference("game_bar_single_tap_enable")
        singleTapFunctionPref = findPreference("game_bar_single_tap_function")
        doubleTapEnablePref = findPreference("game_bar_doubletap_enable")
        doubleTapFunctionPref = findPreference("game_bar_doubletap_function")
        longPressEnablePref = findPreference("game_bar_longpress_enable")
        longPressFunctionPref = findPreference("game_bar_longpress_function")
        longPressTimeoutPref = findPreference("game_bar_longpress_timeout")

        textSizePref = findPreference("game_bar_text_size")
        bgAlphaPref = findPreference("game_bar_background_alpha")
        cornerRadiusPref = findPreference("game_bar_corner_radius")
        paddingPref = findPreference("game_bar_padding")
        itemSpacingPref = findPreference("game_bar_item_spacing")

        updateIntervalPref = findPreference("game_bar_update_interval")
        textColorPref = findPreference("game_bar_text_color")
        titleColorPref = findPreference("game_bar_title_color")
        valueColorPref = findPreference("game_bar_value_color")
        backgroundColorPref = findPreference("game_bar_background_color")
        positionPref = findPreference("game_bar_position")
        splitModePref = findPreference("game_bar_split_mode")
        overlayFormatPref = findPreference("game_bar_format")

        setupPerAppConfig()
        setupMasterSwitchListener()
        setupFeatureSwitchListeners()
        
        fpsDisplayModePref?.isVisible = fpsSwitch?.isChecked ?: true
        
        // Set initial visibility of gesture function selectors
        singleTapFunctionPref?.isVisible = singleTapEnablePref?.isChecked ?: true
        doubleTapFunctionPref?.isVisible = doubleTapEnablePref?.isChecked ?: true
        longPressFunctionPref?.isVisible = longPressEnablePref?.isChecked ?: true
        longPressTimeoutPref?.isVisible = longPressEnablePref?.isChecked ?: true
        
        setupGesturePrefListeners()
        setupStylePrefListeners()
        setupExpandableCategories()
    }
    
    private fun setupExpandableCategories() {
        // Get all expandable categories by their keys
        val overlayFeaturesCategory: ExpandablePreferenceCategory? = findPreference("category_overlay_features")
        val fpsMethodCategory: ExpandablePreferenceCategory? = findPreference("category_fps_method")
        val customizationCategory: ExpandablePreferenceCategory? = findPreference("category_customization")
        val splitConfigCategory: ExpandablePreferenceCategory? = findPreference("category_split_config")
        val gestureControlsCategory: ExpandablePreferenceCategory? = findPreference("category_gesture_controls")
        val perAppCategory: ExpandablePreferenceCategory? = findPreference("category_per_app")
        
        // Categories are collapsed by default
        // You can expand some by default if needed, e.g.:
        // overlayFeaturesCategory?.setExpanded(true)
    }

    private fun setupPerAppConfig() {
        val perAppConfigPref: Preference? = findPreference("game_bar_per_app_config")
        perAppConfigPref?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), GameBarPerAppConfigActivity::class.java))
            true
        }
        
        val fontSelectorPref: Preference? = findPreference("game_bar_font_selector")
        fontSelectorPref?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), GameBarFontSelectorActivity::class.java))
            true
        }
        
        // Preset management preferences
        setupPresetPreferences()
    }
    
    private fun setupPresetPreferences() {
        val presetManager = PresetManager.getInstance(requireContext())
        
        // Save current configuration
        val savePresetPref: Preference? = findPreference("preset_save_current")
        savePresetPref?.setOnPreferenceClickListener {
            showSavePresetDialog(presetManager)
            true
        }
        
        // Import preset
        val importPresetPref: Preference? = findPreference("preset_import")
        importPresetPref?.setOnPreferenceClickListener {
            importPresetLauncher.launch("application/json")
            true
        }
        
        // Manage presets
        val managePresetsPref: Preference? = findPreference("preset_manage")
        managePresetsPref?.setOnPreferenceClickListener {
            val intent = Intent(requireContext(), PresetManagementActivity::class.java)
            presetManagementLauncher.launch(intent)
            true
        }
        
        // Reset to defaults
        val resetDefaultPref: Preference? = findPreference("preset_reset_default")
        resetDefaultPref?.setOnPreferenceClickListener {
            showResetDefaultDialog(presetManager)
            true
        }
    }
    
    private fun showSavePresetDialog(presetManager: PresetManager) {
        val input = android.widget.EditText(requireContext()).apply {
            hint = getString(R.string.hint_preset_name)
            setPadding(50, 30, 50, 30)
        }
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_save_preset)
            .setMessage(R.string.dialog_message_preset_name)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    android.widget.Toast.makeText(requireContext(), R.string.toast_preset_name_empty, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    if (presetManager.savePreset(name)) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_preset_saved, name),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        android.widget.Toast.makeText(requireContext(), R.string.toast_preset_save_failed, android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun showResetDefaultDialog(presetManager: PresetManager) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.preset_reset_default)
            .setMessage(R.string.dialog_message_reset_default)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (presetManager.resetToDefaults()) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        R.string.toast_preset_reset_success,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    
                    // Refresh UI to show default values
                    activity?.recreate()
                } else {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Failed to reset settings",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun importPresetFromUri(uri: Uri) {
        try {
            val presetManager = PresetManager.getInstance(requireContext())
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            
            if (inputStream != null) {
                val file = java.io.File(requireContext().cacheDir, "temp_preset.json")
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()
                
                if (presetManager.importPreset(file)) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        R.string.toast_preset_imported,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    
                    // Clean up temp file
                    file.delete()
                } else {
                    android.widget.Toast.makeText(
                        requireContext(),
                        R.string.toast_preset_import_failed,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                android.widget.Toast.makeText(
                    requireContext(),
                    R.string.toast_preset_import_failed,
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("GameBarFragment", "Failed to import preset", e)
            android.widget.Toast.makeText(
                requireContext(),
                R.string.toast_preset_import_failed,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupMasterSwitchListener() {
        masterSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                if (Settings.canDrawOverlays(requireContext())) {
                    android.util.Log.d("GameBarFragment", "Enabling GameBar from settings")
                    
                    // Ensure we have a fresh GameBar instance
                    if (!GameBar.isInstanceCreated()) {
                        gameBar = GameBar.getInstance(requireContext())
                    }
                    
                    // Clean up any existing instance before creating new one
                    gameBar?.hide()
                    gameBar?.applyPreferences()
                    gameBar?.show()
                    requireContext().startService(Intent(requireContext(), GameBarMonitorService::class.java))
                    true
                } else {
                    Toast.makeText(requireContext(), R.string.overlay_permission_required, Toast.LENGTH_SHORT).show()
                    false
                }
            } else {
                android.util.Log.d("GameBarFragment", "Disabling GameBar from settings")
                
                gameBar?.hide()
                
                // Destroy singleton to ensure clean state
                GameBar.destroyInstance()
                
                // Only stop monitor service if auto-enable is also disabled
                if (autoEnableSwitch?.isChecked != true) {
                    requireContext().stopService(Intent(requireContext(), GameBarMonitorService::class.java))
                }
                true
            }
        }
    }

    private fun setupAutoEnableSwitchListener() {
        autoEnableSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val autoEnabled = newValue as Boolean
            if (autoEnabled) {
                requireContext().startService(Intent(requireContext(), GameBarMonitorService::class.java))
            } else {
                if (masterSwitch?.isChecked != true) {
                    requireContext().stopService(Intent(requireContext(), GameBarMonitorService::class.java))
                }
            }
            true
        }
    }

    private fun setupFeatureSwitchListeners() {
        fpsSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            gameBar?.setShowFps(enabled)
            fpsDisplayModePref?.isVisible = enabled
            true
        }
        fpsDisplayModePref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    gameBar?.applyPreferences()
                    if (GameBar.isShowing()) {
                        gameBar?.hide()
                        gameBar?.show()
                    }
                }
            }
            true
        }
        frameTimeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowFrameTime(newValue as Boolean)
            true
        }
        batteryTempSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowBatteryTemp(newValue as Boolean)
            true
        }
        cpuUsageSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowCpuUsage(newValue as Boolean)
            true
        }
        cpuClockSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowCpuClock(newValue as Boolean)
            true
        }
        cpuTempSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowCpuTemp(newValue as Boolean)
            true
        }
        ramSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowRam(newValue as Boolean)
            true
        }
        gpuUsageSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowGpuUsage(newValue as Boolean)
            true
        }
        gpuClockSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowGpuClock(newValue as Boolean)
            true
        }
        gpuTempSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowGpuTemp(newValue as Boolean)
            true
        }
        ramSpeedSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowRamSpeed(newValue as Boolean)
            true
        }
        ramTempSwitch?.setOnPreferenceChangeListener { _, newValue ->
            gameBar?.setShowRamTemp(newValue as Boolean)
            true
        }
    }

    private fun setupGesturePrefListeners() {
        // Single tap enable/disable and visibility
        singleTapEnablePref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            singleTapFunctionPref?.isVisible = enabled
            true
        }
        
        // Single tap function change
        singleTapFunctionPref?.setOnPreferenceChangeListener { _, _ ->
            // Reload preferences to apply new function
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                gameBar?.applyPreferences()
            }
            true
        }
        
        // Double tap enable/disable and visibility
        doubleTapEnablePref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            doubleTapFunctionPref?.isVisible = enabled
            true
        }
        
        // Double tap function change
        doubleTapFunctionPref?.setOnPreferenceChangeListener { _, _ ->
            // Reload preferences to apply new function
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                gameBar?.applyPreferences()
            }
            true
        }
        
        // Long press enable/disable and visibility
        longPressEnablePref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            longPressFunctionPref?.isVisible = enabled
            longPressTimeoutPref?.isVisible = enabled
            true
        }
        
        // Long press function change
        longPressFunctionPref?.setOnPreferenceChangeListener { _, _ ->
            // Reload preferences to apply new function
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                gameBar?.applyPreferences()
            }
            true
        }
        
        // Long press timeout
        longPressTimeoutPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                val ms = newValue.toLong()
                gameBar?.setLongPressThresholdMs(ms)
            }
            true
        }
    }

    private fun setupStylePrefListeners() {
        textSizePref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updateTextSize(newValue)
            }
            true
        }
        bgAlphaPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updateBackgroundAlpha(newValue)
            }
            true
        }
        cornerRadiusPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updateCornerRadius(newValue)
            }
            true
        }
        paddingPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updatePadding(newValue)
            }
            true
        }
        itemSpacingPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updateItemSpacing(newValue)
            }
            true
        }
        updateIntervalPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                gameBar?.updateUpdateInterval(newValue)
            }
            true
        }
        textColorPref?.setOnPreferenceChangeListener { _, _ ->
            true
        }
        titleColorPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                val hexColor = String.format("#%06X", 0xFFFFFF and newValue)
                gameBar?.updateTitleColor(hexColor)
            }
            true
        }
        valueColorPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                val hexColor = String.format("#%06X", 0xFFFFFF and newValue)
                gameBar?.updateValueColor(hexColor)
            }
            true
        }
        backgroundColorPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                gameBar?.updateBackgroundColor(newValue)
            }
            true
        }
        positionPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                gameBar?.updatePosition(newValue)
            }
            true
        }
        splitModePref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                gameBar?.updateSplitMode(newValue)
            }
            true
        }
        overlayFormatPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                gameBar?.updateOverlayFormat(newValue)
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        
        if (!hasUsageStatsPermission(requireContext())) {
            requestUsageStatsPermission()
        }
        requireContext().let { context ->
            if ((masterSwitch?.isChecked == true) || (autoEnableSwitch?.isChecked == true)) {
                context.startService(Intent(context, GameBarMonitorService::class.java))
            } else {
                context.stopService(Intent(context, GameBarMonitorService::class.java))
            }
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager
            ?: return false
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
    
    private fun refreshPreferences() {
        activity?.recreate()
    }
}