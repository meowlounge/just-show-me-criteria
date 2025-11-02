package dev.prodbyeagle.pinnedAdvancements.client

import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfig
import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.toast.Toast
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.roundToInt

object PinnedAdvancementHudRenderer : HudElement {

    private val TOAST_TEXTURE: Identifier = Identifier.ofVanilla("toast/advancement")
    private const val BASE_WIDTH = Toast.BASE_WIDTH
    private const val BASE_HEIGHT = Toast.BASE_HEIGHT
    private const val TEXT_AREA_START_X = 30
    private const val TEXT_COLOR_DESCRIPTION = 0xFFAAAAAA.toInt()
    private const val TEXT_COLOR_PROGRESS = 0xFFEFEFEF.toInt()
    private const val PROGRESS_BAR_BACKGROUND = 0xB0000000.toInt()
    private const val PROGRESS_BAR_FOREGROUND = 0xFF6AAA2A.toInt()
    private const val ERROR_COLOR = 0xFFFF5555.toInt()

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val config = PinnedAdvancementsConfigManager.config
        if (!config.enabled) {
            return
        }

        val display = PinnedAdvancementTracker.display
        val errorMessage = PinnedAdvancementTracker.error
        if (display == null && errorMessage == null) {
            return
        }

        val width = BASE_WIDTH
        val height = BASE_HEIGHT

        val (x, y) = resolvePosition(context, width, height, config.hud)
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TOAST_TEXTURE, x, y, width, height)

        if (display != null) {
            drawDisplay(context, x, y, width, height, display, config)
        } else if (errorMessage != null) {
            drawError(context, x, y, errorMessage)
        }
    }

    private fun drawDisplay(
        context: DrawContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        display: PinnedAdvancementDisplay,
        config: PinnedAdvancementsConfig
    ) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        val defaultTitleColor = parseColor(
            config.style.headerColor,
            if (display.isChallenge) -30465 else -256
        )
        val titleColor = parseColor(config.style.titleColor, defaultTitleColor)
        val descriptionColor = parseColor(config.style.descriptionColor, TEXT_COLOR_DESCRIPTION)
        val progressTextColor = parseColor(config.style.progressTextColor, TEXT_COLOR_PROGRESS)
        val progressBarColor = parseColor(config.style.progressBarColor, PROGRESS_BAR_FOREGROUND)
        val progressBarBackgroundColor = parseColor(config.style.progressBarBackgroundColor, PROGRESS_BAR_BACKGROUND)

        val textX = x + TEXT_AREA_START_X
        var textY = y + 5

        if (display.titleLines.isNotEmpty()) {
            textY = drawLines(context, textRenderer, display.titleLines, textX, textY, titleColor)
        }

        if (display.descriptionLines.isNotEmpty()) {
            drawLines(context, textRenderer, display.descriptionLines, textX, textY, descriptionColor)
        }

        if (display.progressText != null && config.showProgressText) {
            val progressY = y + height - 12
            context.drawText(textRenderer, display.progressText, textX, progressY, progressTextColor, false)
        }

        if (config.showProgressBar) {
            val barWidth = width - TEXT_AREA_START_X - 12
            val barY = y + height - 6
            context.fill(textX, barY, textX + barWidth, barY + 2, progressBarBackgroundColor)
            val filled = (display.progressPercentage.coerceIn(0f, 1f) * barWidth).roundToInt()
            if (filled > 0) {
                context.fill(textX, barY, textX + filled, barY + 2, progressBarColor)
            }
        }

        display.icon?.let {
            context.drawItemWithoutEntity(it, x + 8, y + 8)
        }
    }

    private fun drawError(context: DrawContext, x: Int, y: Int, message: Text) {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val lines = textRenderer.wrapLines(message, 120)
        var textY = y + 11
        for (line in lines) {
            context.drawText(textRenderer, line, x + TEXT_AREA_START_X, textY, ERROR_COLOR, false)
            textY += 9
        }
    }

    private fun drawLines(
        context: DrawContext,
        textRenderer: net.minecraft.client.font.TextRenderer,
        lines: List<OrderedText>,
        x: Int,
        startY: Int,
        color: Int
    ): Int {
        var y = startY
        lines.forEach { line ->
            context.drawText(textRenderer, line, x, y, color, false)
            y += 9
        }
        return y
    }

    private fun resolvePosition(
        context: DrawContext,
        width: Int,
        height: Int,
        hud: PinnedAdvancementsConfig.HudConfig
    ): Pair<Int, Int> {
        val screenWidth = context.scaledWindowWidth
        val screenHeight = context.scaledWindowHeight

        val x = when (hud.anchor) {
            PinnedAdvancementsConfig.HudConfig.Anchor.TOP_LEFT -> hud.offsetX
            PinnedAdvancementsConfig.HudConfig.Anchor.TOP_RIGHT -> screenWidth - width - hud.offsetX
            PinnedAdvancementsConfig.HudConfig.Anchor.BOTTOM_LEFT -> hud.offsetX
            PinnedAdvancementsConfig.HudConfig.Anchor.BOTTOM_RIGHT -> screenWidth - width - hud.offsetX
        }.coerceAtLeast(0)

        val y = when (hud.anchor) {
            PinnedAdvancementsConfig.HudConfig.Anchor.TOP_LEFT -> hud.offsetY
            PinnedAdvancementsConfig.HudConfig.Anchor.TOP_RIGHT -> hud.offsetY
            PinnedAdvancementsConfig.HudConfig.Anchor.BOTTOM_LEFT -> screenHeight - height - hud.offsetY
            PinnedAdvancementsConfig.HudConfig.Anchor.BOTTOM_RIGHT -> screenHeight - height - hud.offsetY
        }.coerceAtLeast(0)

        return Pair(x, y)
    }

    private fun parseColor(value: String?, fallback: Int): Int {
        if (value.isNullOrBlank()) {
            return fallback
        }

        val normalized = value.trim()
        val hex = when {
            normalized.startsWith("#") -> normalized.substring(1)
            normalized.startsWith("0x", ignoreCase = true) -> normalized.substring(2)
            else -> normalized
        }

        return try {
            when (hex.length) {
                6 -> (0xFF shl 24) or hex.toInt(16)
                8 -> hex.toLong(16).toInt()
                else -> fallback
            }
        } catch (_: NumberFormatException) {
            fallback
        }
    }
}
