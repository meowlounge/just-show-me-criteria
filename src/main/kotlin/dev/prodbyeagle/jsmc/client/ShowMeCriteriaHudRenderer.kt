package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.client.hud.CriteriaPanelMeasurer
import dev.prodbyeagle.jsmc.client.hud.CriteriaPanelPainter
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

object ShowMeCriteriaHudRenderer : HudElement {

    private const val MIN_SCALE = 0.5f
    private const val MAX_SCALE = 3.0f
    private val measurer = CriteriaPanelMeasurer()
    private val painter = CriteriaPanelPainter()

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val config = ShowMeCriteriaConfigManager.config
        if (!config.enabled) return

        val display = ShowMeCriteriaTracker.display
        val error = ShowMeCriteriaTracker.error
        if (display == null && error == null) return

        val textRenderer = MinecraftClient.getInstance().textRenderer
        val lineHeight = textRenderer.fontHeight + 3
        val scale = config.hud.scale.toFloat().coerceIn(MIN_SCALE, MAX_SCALE)

        val metrics = if (display != null) {
            measurer.displayMetrics(display, lineHeight) { textRenderer.getWidth(it) }
        } else {
            measurer.errorMetrics(error!!, lineHeight, { msg -> textRenderer.wrapLines(msg, 240) }) {
                textRenderer.getWidth(it)
            }
        }

        val scaledWidth = (metrics.width * scale).toInt()
        val scaledHeight = (metrics.height * scale).toInt()
        val (x, y) = resolvePosition(context, scaledWidth, scaledHeight, config.hud)

        context.matrices.pushMatrix()
        context.matrices.translate(x.toFloat(), y.toFloat())
        context.matrices.scale(scale, scale)

        if (display != null) {
            painter.drawDisplay(context, display, config, metrics, lineHeight)
        } else if (error != null) {
            painter.drawError(context, error, metrics, lineHeight) { msg -> textRenderer.wrapLines(msg, 240) }
        }

        context.matrices.popMatrix()
    }

    private fun resolvePosition(
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
