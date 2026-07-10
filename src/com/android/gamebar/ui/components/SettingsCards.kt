/*
 * SPDX-FileCopyrightText: 2026 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.gamebar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.gamebar.R
import com.android.settingslib.widget.LottieColorUtils
import com.android.gamebar.ui.theme.UiStyleController
import kotlin.math.roundToInt

enum class GameBarNavTab(val title: String, val iconRes: Int) {
    HOME("Home", R.drawable.ic_nav_home),
    FEATURES("Features", R.drawable.ic_nav_features),
    CUSTOMIZATION("Customization", R.drawable.ic_nav_customization),
    FPS_RECORD("FPS Record", R.drawable.ic_nav_fps_record),
    PRESETS("Presets", R.drawable.ic_nav_presets),
}

data class SelectOption(val value: String, val label: String)

@Composable
fun GameBarLottieCard() {
    SettingsSectionCard(
        title = "GameBar",
        summary = null,
        showHeader = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AndroidView(
                factory = { context ->
                    LottieAnimationView(context).apply {
                        setAnimation(R.raw.gamebar)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        LottieColorUtils.applyDynamicColors(context, this)
                        repeatCount = LottieDrawable.INFINITE
                        repeatMode = LottieDrawable.RESTART
                        playAnimation()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}

@Composable
fun HeaderCard(title: String, summary: String) {
    val context = LocalContext.current
    UiStyleController.ensureInitialized(context)
    val amoledBlackEnabled by UiStyleController.amoledBlackEnabled.collectAsState()
    var visible by remember { mutableStateOf(false) }
    val headerShadow = Shadow(
        color = Color.Black.copy(alpha = 0.42f),
        offset = androidx.compose.ui.geometry.Offset(0f, 2f),
        blurRadius = 8f
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
    ) {
        val transition = rememberInfiniteTransition(label = "gb_header_gradient")
        val shift by transition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing)
            ),
            label = "gb_header_shift"
        )
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = if (amoledBlackEnabled) Color(0xFF121212) else MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(900f * shift, 600f * (1.1f - shift))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gamebar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(shadow = headerShadow),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyMedium.copy(shadow = headerShadow),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { visible = true }
}

@Composable
fun GameBarFloatingBottomNavBar(
    selected: GameBarNavTab,
    onTabSelected: (GameBarNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    UiStyleController.ensureInitialized(context)
    val amoledBlackEnabled by UiStyleController.amoledBlackEnabled.collectAsState()
    val tabs = GameBarNavTab.entries
    val selectedIndex = tabs.indexOf(selected).coerceAtLeast(0)
    val animatedSelectedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gamebar_nav_index"
    )
    val itemSize = 52.dp
    val itemSpacing = 6.dp
    val containerPadding = 8.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        val navWidth = (itemSize * tabs.size) + (itemSpacing * (tabs.size - 1)) + (containerPadding * 2)
        Surface(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .width(navWidth)
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.38f),
                    spotColor = Color.Black.copy(alpha = 0.54f),
                ),
            shape = RoundedCornerShape(28.dp),
            color = Color.Transparent,
            border = BorderStroke(
                width = 1.dp,
                color = if (amoledBlackEnabled) {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                }
            ),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (amoledBlackEnabled) {
                                listOf(Color(0xFF1A1A1A), Color(0xFF0E0E0E))
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.98f),
                                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.98f)
                                )
                            }
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = containerPadding)
            ) {
                val stepPx = with(androidx.compose.ui.platform.LocalDensity.current) { (itemSize + itemSpacing).toPx() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                ) {
                    val indicatorOffsetX = stepPx * animatedSelectedIndex
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .offset { IntOffset(indicatorOffsetX.roundToInt(), 0) }
                            .width(itemSize)
                            .height(itemSize)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(18.dp))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEach { tab ->
                        val isSelected = tab == selected
                        Box(
                            modifier = Modifier
                                .width(itemSize)
                                .height(itemSize)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onTabSelected(tab) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = tab.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    summary: String? = null,
    showHeader: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    UiStyleController.ensureInitialized(context)
    val amoledBlackEnabled by UiStyleController.amoledBlackEnabled.collectAsState()
    val cardShape = RoundedCornerShape(26.dp)
    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (amoledBlackEnabled) Color(0xFF121212) else MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    shape = cardShape
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showHeader) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (!summary.isNullOrBlank()) {
                    Text(text = summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}

@Composable
fun SectionSubHeader(
    title: String,
    summary: String,
) {
    val context = LocalContext.current
    UiStyleController.ensureInitialized(context)
    val amoledBlackEnabled by UiStyleController.amoledBlackEnabled.collectAsState()
    val headerTextShadow = Shadow(
        color = Color.Black.copy(alpha = 0.42f),
        offset = androidx.compose.ui.geometry.Offset(0f, 2f),
        blurRadius = 8f
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (amoledBlackEnabled) Color(0xFF171717) else MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            if (amoledBlackEnabled) Color(0xFF171717) else MaterialTheme.colorScheme.secondaryContainer,
                            if (amoledBlackEnabled) Color(0xFF1E2833) else MaterialTheme.colorScheme.primaryContainer
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(800f, 500f)
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(shadow = headerTextShadow),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium.copy(shadow = headerTextShadow),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsCustomSliderRow(
    title: String,
    value: Int,
    min: Int,
    max: Int,
    defaultValue: Int,
    units: String = "",
    showSign: Boolean = false,
    onValueCommitted: (Int) -> Unit,
) {
    var sliderValue by remember(value) { mutableStateOf(value.toFloat()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()
    val shownValue = sliderValue.roundToInt().coerceIn(min, max)
    val valueText = buildString {
        if (showSign && shownValue > 0) append("+")
        append(shownValue)
        if (units.isNotBlank()) {
            append(" ")
            append(units)
        }
        if (!isDragging && shownValue == defaultValue) {
            append(" (Default)")
        }
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    valueText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (!isDragging && shownValue != defaultValue) {
                    IconButton(
                        onClick = {
                            sliderValue = defaultValue.toFloat()
                            onValueCommitted(defaultValue)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Restore,
                            contentDescription = "Reset to default",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            interactionSource = interactionSource,
            valueRange = min.toFloat()..max.toFloat(),
            onValueChangeFinished = {
                onValueCommitted(sliderValue.roundToInt().coerceIn(min, max))
            }
        )
    }
}

@Composable
fun HomeMenuCard(
    title: String,
    summary: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    UiStyleController.ensureInitialized(context)
    val amoledBlackEnabled by UiStyleController.amoledBlackEnabled.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (amoledBlackEnabled) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SettingsSelectRow(
    title: String,
    selectedValue: String,
    options: List<SelectOption>,
    enabled: Boolean = true,
    onValueSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label ?: selectedValue
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.widthIn(min = 150.dp, max = 210.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { expanded = true }
            ) {
                Text(
                    text = selectedLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            expanded = false
                            onValueSelected(option.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    summary: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(text = summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
