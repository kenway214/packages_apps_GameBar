/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.gamebar

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PresetManagementActivity : CollapsingToolbarBaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var emptyTextView: TextView
    private lateinit var presetAdapter: PresetAdapter
    private lateinit var presetManager: PresetManager

    companion object {
        private const val REQUEST_CODE_IMPORT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preset_management)
        
        title = getString(R.string.preset_saved_presets)
        
        presetManager = PresetManager.getInstance(this)
        
        initViews()
        setupRecyclerView()
        loadPresets()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rv_presets)
        emptyView = findViewById(R.id.empty_state)
        emptyTextView = findViewById(R.id.tv_empty_message)
        
        emptyTextView.text = getString(R.string.preset_no_presets_desc)
    }

    private fun setupRecyclerView() {
        presetAdapter = PresetAdapter(
            presets = emptyList(),
            onPresetClick = { preset ->
                showLoadPresetDialog(preset)
            },
            onMenuClick = { preset, view ->
                showPresetMenu(preset, view)
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = presetAdapter
    }

    private fun loadPresets() {
        val presets = presetManager.getAllPresets()
        presetAdapter.updatePresets(presets)
        
        if (presets.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun showLoadPresetDialog(preset: PresetManager.Preset) {
        AlertDialog.Builder(this)
            .setTitle(preset.name)
            .setMessage(R.string.dialog_message_load_preset)
            .setPositiveButton(R.string.preset_action_load) { _, _ ->
                loadPreset(preset)
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    private fun loadPreset(preset: PresetManager.Preset) {
        if (presetManager.loadPreset(preset.id)) {
            Toast.makeText(
                this,
                getString(R.string.toast_preset_loaded, preset.name),
                Toast.LENGTH_SHORT
            ).show()
            
            // Set result to notify parent activity to refresh
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, R.string.toast_preset_load_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPresetMenu(preset: PresetManager.Preset, anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menu.add(0, 1, 0, R.string.preset_action_load)
        popupMenu.menu.add(0, 2, 0, R.string.preset_action_export)
        popupMenu.menu.add(0, 3, 0, R.string.preset_action_rename)
        popupMenu.menu.add(0, 4, 0, R.string.preset_action_delete)
        
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showLoadPresetDialog(preset)
                    true
                }
                2 -> {
                    exportPreset(preset)
                    true
                }
                3 -> {
                    showRenameDialog(preset)
                    true
                }
                4 -> {
                    showDeleteDialog(preset)
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }

    private fun exportPreset(preset: PresetManager.Preset) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "GameBar_Preset_${preset.name.replace(" ", "_")}_$timeStamp.json"
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            
            if (presetManager.exportPreset(preset.id, file)) {
                // Share the exported file
                val uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "GameBar Preset: ${preset.name}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(shareIntent, getString(R.string.preset_action_export)))
                
                Toast.makeText(
                    this,
                    getString(R.string.toast_preset_exported, file.absolutePath),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, R.string.toast_preset_export_failed, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.toast_preset_export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRenameDialog(preset: PresetManager.Preset) {
        val input = EditText(this).apply {
            setText(preset.name)
            hint = getString(R.string.hint_preset_name)
            setPadding(50, 30, 50, 30)
        }
        
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_rename_preset)
            .setView(input)
            .setPositiveButton(R.string.preset_action_rename) { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isEmpty()) {
                    Toast.makeText(this, R.string.toast_preset_name_empty, Toast.LENGTH_SHORT).show()
                } else {
                    if (presetManager.renamePreset(preset.id, newName)) {
                        Toast.makeText(this, R.string.toast_preset_renamed, Toast.LENGTH_SHORT).show()
                        loadPresets()
                    } else {
                        Toast.makeText(this, R.string.toast_preset_save_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    private fun showDeleteDialog(preset: PresetManager.Preset) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_delete_preset)
            .setMessage(getString(R.string.dialog_message_delete_preset, preset.name))
            .setPositiveButton(R.string.preset_action_delete) { _, _ ->
                if (presetManager.deletePreset(preset.id)) {
                    Toast.makeText(this, R.string.toast_preset_deleted, Toast.LENGTH_SHORT).show()
                    loadPresets()
                } else {
                    Toast.makeText(this, R.string.toast_preset_delete_failed, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadPresets()
    }
}
