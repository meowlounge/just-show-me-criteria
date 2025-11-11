package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.client.hud.HudPanelLayout
import dev.prodbyeagle.jsmc.client.hud.HudPanelMetrics
import dev.prodbyeagle.jsmc.client.hud.HudPanelPainter
import dev.prodbyeagle.jsmc.client.hud.HudPanelStyle
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

object ShowMeCriteriaHudRenderer : HudElement {

    private const val MIN_SCALE = 0.5f
    private const val MAX_SCALE = 3.0f

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val config = ShowMeCriteriaConfigManager.config
        if (!config.enabled) return

        val display = ShowMeCriteriaTracker.display
        val error = ShowMeCriteriaTracker.error
        if (display == null && error == null) return

        val textRenderer = MinecraftClient.getInstance().textRenderer
        val layout = ensureLayout(textRenderer)
        val painter = ensurePainter(textRenderer)

        val lineHeight = textRenderer.fontHeight + 3
        val scale = config.hud.scale.toFloat().coerceIn(MIN_SCALE, MAX_SCALE)
        val errorLines = error?.let(layout::wrapErrorLines)

        val metrics: HudPanelMetrics = when {
            display != null -> layout.measureDisplay(display, lineHeight)
            else -> layout.measureError(errorLines ?: emptyList(), lineHeight)
        }

        val scaledWidth = (metrics.width * scale).toInt()
        val scaledHeight = (metrics.height * scale).toInt()
        val (x, y) = layout.resolvePosition(context, scaledWidth, scaledHeight, config.hud)

        context.matrices.pushMatrix()
        context.matrices.translate(x.toFloat(), y.toFloat())
        context.matrices.scale(scale, scale)

        val opacity = config.style.normalizedOpacity()
        val colors = resolveColors(config.style)
        when {
            display != null -> painter.drawDisplay(
                context,
                display,
                metrics,
                lineHeight,
                opacity,
                colors.header,
                colors.entry,
                colors.overflow
            )
            errorLines != null -> painter.drawError(context, errorLines, metrics, lineHeight, opacity)
        }

        context.matrices.popMatrix()
    }

    private fun ensureLayout(textRenderer: TextRenderer): HudPanelLayout {
        if (layoutRenderer !== textRenderer || layout == null) {
            layoutRenderer = textRenderer
            layout = HudPanelLayout(textRenderer)
        }
        return layout!!
    }

    private fun ensurePainter(textRenderer: TextRenderer): HudPanelPainter {
        if (painterRenderer !== textRenderer || painter == null) {
            painterRenderer = textRenderer
            painter = HudPanelPainter(textRenderer)
        }
        return painter!!
    }

    private fun resolveColors(style: ShowMeCriteriaConfig.StyleConfig): HudColors {
        val snapshot = cachedStyle
        if (snapshot != null && snapshot == style) {
            return cachedColors
        }
        cachedStyle = style.copy()
        cachedColors = HudColors(
            header = HudPanelStyle.parseColor(style.headerColor, HudPanelStyle.FALLBACK_COLOR),
            entry = HudPanelStyle.parseColor(style.descriptionColor, HudPanelStyle.TEXT_COLOR_CRITERIA),
            overflow = HudPanelStyle.parseColor(style.progressTextColor, HudPanelStyle.TEXT_COLOR_MUTED)
        )
        return cachedColors
    }

    private data class HudColors(
        val header: Int,
        val entry: Int,
        val overflow: Int
    )

    private var layoutRenderer: TextRenderer? = null
    private var painterRenderer: TextRenderer? = null
    private var layout: HudPanelLayout? = null
    private var painter: HudPanelPainter? = null
    private var cachedStyle: ShowMeCriteriaConfig.StyleConfig? = null
    private var cachedColors: HudColors = HudColors(
        header = HudPanelStyle.FALLBACK_COLOR,
        entry = HudPanelStyle.TEXT_COLOR_CRITERIA,
        overflow = HudPanelStyle.TEXT_COLOR_MUTED
    )
}
