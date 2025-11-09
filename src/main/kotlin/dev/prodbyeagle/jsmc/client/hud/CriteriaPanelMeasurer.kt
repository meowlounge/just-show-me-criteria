package dev.prodbyeagle.jsmc.client.hud

import dev.prodbyeagle.jsmc.client.ShowMeCriteriaDisplay
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

internal class CriteriaPanelMeasurer {

    fun displayMetrics(
        display: ShowMeCriteriaDisplay,
        lineHeight: Int,
        textWidth: (OrderedText) -> Int
    ): PanelMetrics {
        val headerWidth = textWidth(PanelStyle.HEADER_TEXT)
        val bodyWidth = when {
            display.criteria.remainingCount == 0 -> textWidth(PanelStyle.COMPLETE_TEXT)
            display.criteria.overflowCount > 0   -> {
                val overflow = Text.literal("${display.criteria.overflowCount} more...").asOrderedText()
                (display.criteria.lines + overflow).maxOf(textWidth)
            }
            else -> display.criteria.lines.maxOf(textWidth)
        }

        val width = PanelStyle.PADDING_X * 2 + maxOf(PanelStyle.MIN_CONTENT_WIDTH, headerWidth, bodyWidth)
        val bodyLines = if (display.criteria.remainingCount == 0) {
            1
        } else {
            display.criteria.lines.size + if (display.criteria.overflowCount > 0) 1 else 0
        }
        val totalLines = 1 + bodyLines
        val height = PanelStyle.PADDING_Y * 2 + totalLines * lineHeight

        return PanelMetrics(width, height)
    }

    fun errorMetrics(
        message: Text,
        lineHeight: Int,
        wrap: (Text) -> List<OrderedText>,
        widthProvider: (OrderedText) -> Int
    ): PanelMetrics {
        val lines = wrap(message)
        val maxWidth = lines.maxOfOrNull(widthProvider) ?: PanelStyle.MIN_CONTENT_WIDTH
        val width = PanelStyle.PADDING_X * 2 + maxWidth
        val height = PanelStyle.PADDING_Y * 2 + lineHeight * lines.size.coerceAtLeast(1)
        return PanelMetrics(width, height)
    }
}
