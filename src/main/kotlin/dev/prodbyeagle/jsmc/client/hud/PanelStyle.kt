package dev.prodbyeagle.jsmc.client.hud

import net.minecraft.text.Text

internal object PanelStyle {
    const val PADDING_X = 16
    const val PADDING_Y = 12
    const val MIN_CONTENT_WIDTH = 140
    const val BACKGROUND = 0xD0151515.toInt()
    const val BORDER = 0x50FFFFFF
    const val TEXT_COLOR_CRITERIA = 0xFFE4E4E4.toInt()
    const val TEXT_COLOR_MUTED = 0xFF9CA3AF.toInt()
    const val ERROR_COLOR = 0xFFFF5555.toInt()
    const val FALLBACK_COLOR = 0xFFFFFFFF.toInt()
    val HEADER_TEXT = Text.literal("Remaining Criteria").asOrderedText()
    val COMPLETE_TEXT = Text.literal("All criteria complete").asOrderedText()
}
