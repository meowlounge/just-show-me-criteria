package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.client.hud.HudPanelLayout
import dev.prodbyeagle.jsmc.client.hud.HudPanelPainter
import dev.prodbyeagle.jsmc.client.hud.HudPanelMetrics
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
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
        val layout = HudPanelLayout(textRenderer)
        val painter = HudPanelPainter(textRenderer)

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
        when {
            display != null -> painter.drawDisplay(context, display, metrics, lineHeight, opacity, config)
            error != null && errorLines != null -> painter.drawError(context, errorLines, metrics, lineHeight, opacity)
        }

        context.matrices.popMatrix()
    }
}
