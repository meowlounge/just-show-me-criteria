package dev.prodbyeagle.jsmc.client.hud

import dev.prodbyeagle.jsmc.client.ShowMeCriteriaDisplay
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

internal class HudPanelLayout(
    private val textRenderer: TextRenderer
) {

    fun measureDisplay(display: ShowMeCriteriaDisplay, lineHeight: Int): HudPanelMetrics {
        val headerWidth = textRenderer.getWidth(HudPanelStyle.headerText)
        val bodyWidth = when {
            display.criteria.remainingCount == 0 -> textRenderer.getWidth(HudPanelStyle.completeText)
            display.criteria.overflowCount > 0 -> {
                val overflow = HudPanelStyle.overflowText(display.criteria.overflowCount)
                (display.criteria.lines + overflow).maxOfOrNull(textRenderer::getWidth) ?: 0
            }
            else -> display.criteria.lines.maxOfOrNull(textRenderer::getWidth) ?: 0
        }

        val contentWidth = maxOf(HudPanelStyle.MIN_CONTENT_WIDTH, headerWidth, bodyWidth)
        val bodyLines = when {
            display.criteria.remainingCount == 0 -> 1
            display.criteria.overflowCount > 0 -> display.criteria.lines.size + 1
            else -> display.criteria.lines.size
        }
        val totalLines = 1 + bodyLines
        val height = HudPanelStyle.PADDING_Y * 2 + totalLines * lineHeight
        val width = HudPanelStyle.PADDING_X * 2 + contentWidth
        return HudPanelMetrics(width, height)
    }

    fun measureError(lines: List<OrderedText>, lineHeight: Int): HudPanelMetrics {
        val widest = lines.maxOfOrNull(textRenderer::getWidth) ?: HudPanelStyle.MIN_CONTENT_WIDTH
        val width = HudPanelStyle.PADDING_X * 2 + maxOf(HudPanelStyle.MIN_CONTENT_WIDTH, widest)
        val height = HudPanelStyle.PADDING_Y * 2 + lineHeight * lines.size.coerceAtLeast(1)
        return HudPanelMetrics(width, height)
    }

    fun wrapErrorLines(error: Text): List<OrderedText> =
        textRenderer.wrapLines(error, 240)

    fun resolvePosition(
        context: DrawContext,
        width: Int,
        height: Int,
        hud: ShowMeCriteriaConfig.HudConfig
    ): Pair<Int, Int> {
        val screenWidth = context.scaledWindowWidth
        val screenHeight = context.scaledWindowHeight

        val x = when (hud.anchor) {
            ShowMeCriteriaConfig.HudConfig.Anchor.TOP_LEFT -> hud.offsetX
            ShowMeCriteriaConfig.HudConfig.Anchor.TOP_RIGHT -> screenWidth - width - hud.offsetX
            ShowMeCriteriaConfig.HudConfig.Anchor.BOTTOM_LEFT -> hud.offsetX
            ShowMeCriteriaConfig.HudConfig.Anchor.BOTTOM_RIGHT -> screenWidth - width - hud.offsetX
        }.coerceAtLeast(0)

        val y = when (hud.anchor) {
            ShowMeCriteriaConfig.HudConfig.Anchor.TOP_LEFT -> hud.offsetY
            ShowMeCriteriaConfig.HudConfig.Anchor.TOP_RIGHT -> hud.offsetY
            ShowMeCriteriaConfig.HudConfig.Anchor.BOTTOM_LEFT -> screenHeight - height - hud.offsetY
            ShowMeCriteriaConfig.HudConfig.Anchor.BOTTOM_RIGHT -> screenHeight - height - hud.offsetY
        }.coerceAtLeast(0)

        return x to y
    }
}
