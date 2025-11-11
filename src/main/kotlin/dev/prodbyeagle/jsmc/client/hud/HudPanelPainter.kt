package dev.prodbyeagle.jsmc.client.hud

import dev.prodbyeagle.jsmc.client.ShowMeCriteriaDisplay
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText

internal class HudPanelPainter(
    private val textRenderer: TextRenderer
) {

    fun drawDisplay(
        context: DrawContext,
        display: ShowMeCriteriaDisplay,
        metrics: HudPanelMetrics,
        lineHeight: Int,
        opacity: Double,
        headerColor: Int,
        entryColor: Int,
        overflowColor: Int
    ) {
        drawBackground(context, metrics, opacity)

        var y = HudPanelStyle.PADDING_Y
        y = drawLine(context, HudPanelStyle.headerText, y, headerColor, lineHeight)

        if (display.criteria.remainingCount == 0) {
            drawLine(context, HudPanelStyle.completeText, y, entryColor, lineHeight)
            return
        }

        display.criteria.lines.forEach { ordered ->
            y = drawLine(context, ordered, y, entryColor, lineHeight)
        }

        if (display.criteria.overflowCount > 0) {
            val overflow = HudPanelStyle.overflowText(display.criteria.overflowCount)
            drawLine(context, overflow, y, overflowColor, lineHeight)
        }
    }

    fun drawError(
        context: DrawContext,
        lines: List<OrderedText>,
        metrics: HudPanelMetrics,
        lineHeight: Int,
        opacity: Double
    ) {
        drawBackground(context, metrics, opacity)
        var y = HudPanelStyle.PADDING_Y
        lines.forEach { line ->
            y = drawLine(context, line, y, HudPanelStyle.ERROR_COLOR, lineHeight)
        }
    }

    private fun drawBackground(context: DrawContext, metrics: HudPanelMetrics, opacity: Double) {
        val background = HudPanelStyle.backgroundColor(opacity)
        val border = HudPanelStyle.borderColor(opacity)
        context.fill(0, 0, metrics.width, metrics.height, background)
        context.fill(0, 0, metrics.width, 1, border)
        context.fill(0, metrics.height - 1, metrics.width, metrics.height, border)
        context.fill(0, 0, 1, metrics.height, border)
        context.fill(metrics.width - 1, 0, metrics.width, metrics.height, border)
    }

    private fun drawLine(
        context: DrawContext,
        ordered: OrderedText,
        startY: Int,
        color: Int,
        lineHeight: Int
    ): Int {
        context.drawText(textRenderer, ordered, HudPanelStyle.PADDING_X, startY, color, true)
        return startY + lineHeight
    }
}
