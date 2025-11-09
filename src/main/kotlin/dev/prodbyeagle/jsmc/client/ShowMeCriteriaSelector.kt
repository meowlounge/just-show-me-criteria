package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import dev.prodbyeagle.jsmc.mixin.AdvancementTabAccessor
import dev.prodbyeagle.jsmc.mixin.AdvancementWidgetAccessor
import dev.prodbyeagle.jsmc.mixin.AdvancementsScreenAccessor
import net.minecraft.advancement.Advancement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

object ShowMeCriteriaSelector {

    private const val WINDOW_WIDTH = 252
    private const val WINDOW_HEIGHT = 140
    private const val TREE_WIDTH = 234
    private const val TREE_HEIGHT = 113

    fun handleRightClick(screen: AdvancementsScreen, click: Click): Boolean {
        val accessor = screen as AdvancementsScreenAccessor
        val selectedTab = accessor.`jsmc$getSelectedTab`() ?: return false
        val tabAccessor = selectedTab as AdvancementTabAccessor

        val baseX = (screen.width - WINDOW_WIDTH) / 2
        val baseY = (screen.height - WINDOW_HEIGHT) / 2

        val relativeX = (click.x() - baseX).toInt()
        val relativeY = (click.y() - baseY).toInt()

        if (relativeX !in 0 until TREE_WIDTH || relativeY !in 0 until TREE_HEIGHT) {
            return false
        }

        val originX = MathHelper.floor(tabAccessor.`jsmc$getOriginX`())
        val originY = MathHelper.floor(tabAccessor.`jsmc$getOriginY`())

        // 26x26 ist die Standardgröße eines Advancement-Icons
        val hoveredWidget = tabAccessor.`jsmc$getWidgets`()
            .firstOrNull { widget ->
                val widgetAccessor = widget as AdvancementWidgetAccessor
                val x = widgetAccessor.`jsmc$getX`() - originX
                val y = widgetAccessor.`jsmc$getY`() - originY
                relativeX in x..(x + 26) && relativeY in y..(y + 26)
            } ?: return false

        val widgetAccessor = hoveredWidget as AdvancementWidgetAccessor
        val entry = widgetAccessor.`jsmc$getAdvancement`().advancementEntry
        val id = entry.id().toString()

        val config = ShowMeCriteriaConfigManager.config
        if (config.pinnedAdvancement == id) {
            return true
        }

        config.pinnedAdvancement = id
        ShowMeCriteriaConfigManager.save()

        val client = MinecraftClient.getInstance()
        ShowMeCriteriaTracker.refreshNow(client)

        val title = Advancement.getNameFromIdentity(entry)
        client.player?.sendMessage(
            Text.literal("Tracking criteria for: ").append(title),
            true
        )

        return true
    }

}
