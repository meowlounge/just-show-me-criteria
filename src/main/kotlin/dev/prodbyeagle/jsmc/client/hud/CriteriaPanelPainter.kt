package dev.prodbyeagle.jsmc.client.hud

import dev.prodbyeagle.jsmc.client.ShowMeCriteriaDisplay
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

internal class CriteriaPanelPainter {

    private val textRenderer get() = MinecraftClient.getInstance().textRenderer

    fun drawDisplay(
        context: DrawContext,
        display: ShowMeCriteriaDisplay,
        config: ShowMeCriteriaConfig,
        metrics: PanelMetrics,
        lineHeight: Int
    ) {
        drawPanelBackground(context, metrics)
        val headerColor = parseColor(config.style.headerColor, PanelStyle.FALLBACK_COLOR)
        val entryColor = parseColor(config.style.descriptionColor, PanelStyle.TEXT_COLOR_CRITERIA)
        val overflowColor = parseColor(config.style.progressTextColor, PanelStyle.TEXT_COLOR_MUTED)

        var y = PanelStyle.PADDING_Y
        y = drawLine(context, PanelStyle.HEADER_TEXT, y, headerColor, lineHeight)

        if (display.criteria.remainingCount == 0) {
            drawLine(context, PanelStyle.COMPLETE_TEXT, y, entryColor, lineHeight)
            return
        }

        display.criteria.lines.forEach { ordered ->
            y = drawLine(context, ordered, y, entryColor, lineHeight)
        }

        if (display.criteria.overflowCount > 0) {
            val overflow = Text.literal("${display.criteria.overflowCount} more...").asOrderedText()
            drawLine(context, overflow, y, overflowColor, lineHeight)
        }
    }

    fun drawError(
        context: DrawContext,
        message: Text,
        metrics: PanelMetrics,
        lineHeight: Int,
        wrap: (Text) -> List<OrderedText>
    ) {
        drawPanelBackground(context, metrics)
        var y = PanelStyle.PADDING_Y
        wrap(message).forEach { ordered ->
            y = drawLine(context, ordered, y, PanelStyle.ERROR_COLOR, lineHeight)
        }
    }

    private fun drawPanelBackground(context: DrawContext, metrics: PanelMetrics) {
        context.fill(0, 0, metrics.width, metrics.height, PanelStyle.BACKGROUND)
        context.fill(0, 0, metrics.width, 1, PanelStyle.BORDER)
        context.fill(0, metrics.height - 1, metrics.width, metrics.height, PanelStyle.BORDER)
        context.fill(0, 0, 1, metrics.height, PanelStyle.BORDER)
        context.fill(metrics.width - 1, 0, metrics.width, metrics.height, PanelStyle.BORDER)
    }

    private fun drawLine(
        context: DrawContext,
        ordered: OrderedText,
        startY: Int,
        color: Int,
        lineHeight: Int
    ): Int {
        context.drawText(textRenderer, ordered, PanelStyle.PADDING_X, startY, color, true)
        return startY + lineHeight
    }

    private fun parseColor(value: String?, fallback: Int): Int {
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
