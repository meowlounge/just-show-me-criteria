package dev.prodbyeagle.jsmc.client.hud

import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import kotlin.math.roundToInt

internal object HudPanelStyle {
    const val PADDING_X = 16
    const val PADDING_Y = 12
    const val MIN_CONTENT_WIDTH = 140
    private const val BACKGROUND_RGB = 0x00151515
    private const val BORDER_RGB = 0x00FFFFFF
    private const val DEFAULT_BORDER_ALPHA = 0x50
    private const val DEFAULT_BACKGROUND_ALPHA = 0xD0
    private const val BORDER_ALPHA_RATIO = DEFAULT_BORDER_ALPHA.toDouble() / DEFAULT_BACKGROUND_ALPHA.toDouble()
    const val TEXT_COLOR_CRITERIA = 0xFFE4E4E4.toInt()
    const val TEXT_COLOR_MUTED = 0xFF9CA3AF.toInt()
    const val ERROR_COLOR = 0xFFFF5555.toInt()
    const val FALLBACK_COLOR = 0xFFFFFFFF.toInt()

    val headerText: OrderedText = Text.literal("Remaining Criteria").asOrderedText()
    val completeText: OrderedText = Text.literal("All criteria complete").asOrderedText()

    fun overflowText(overflow: Int): OrderedText =
        Text.literal("$overflow more...").asOrderedText()

    fun backgroundColor(opacity: Double): Int {
        val alpha = (opacity.coerceIn(0.0, 1.0) * 255).roundToInt().coerceIn(0, 255)
        return (alpha shl 24) or BACKGROUND_RGB
    }

    fun borderColor(opacity: Double): Int {
        val baseAlpha = (opacity.coerceIn(0.0, 1.0) * 255).roundToInt().coerceIn(0, 255)
        val alpha = (baseAlpha * BORDER_ALPHA_RATIO).roundToInt().coerceIn(0, 255)
        return (alpha shl 24) or BORDER_RGB
    }

    fun parseColor(value: String?, fallback: Int): Int {
        if (value.isNullOrBlank()) return fallback
        val normalized = value.trim()
        val hex = when {
            normalized.startsWith("#") -> normalized.substring(1)
            normalized.startsWith("0x", ignoreCase = true) -> normalized.substring(2)
            else -> normalized
        }
        return runCatching {
            when (hex.length) {
                6 -> (0xFF shl 24) or hex.toInt(16)
                8 -> hex.toLong(16).toInt()
                else -> fallback
            }
        }.getOrDefault(fallback)
    }
}
