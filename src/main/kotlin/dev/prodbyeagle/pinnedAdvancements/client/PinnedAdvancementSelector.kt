package dev.prodbyeagle.pinnedAdvancements.client

import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfigManager
import dev.prodbyeagle.pinnedAdvancements.mixin.AdvancementTabAccessor
import dev.prodbyeagle.pinnedAdvancements.mixin.AdvancementWidgetAccessor
import dev.prodbyeagle.pinnedAdvancements.mixin.AdvancementsScreenAccessor
import net.minecraft.advancement.Advancement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

object PinnedAdvancementSelector {

    private const val WINDOW_WIDTH = 252
    private const val WINDOW_HEIGHT = 140
    private const val TREE_WIDTH = 234
    private const val TREE_HEIGHT = 113

    fun handleRightClick(screen: AdvancementsScreen, click: Click): Boolean {
        val accessor = screen as AdvancementsScreenAccessor
        val selectedTab = accessor.pinnedAdvancements_getSelectedTab() ?: return false
        val tabAccessor = selectedTab as AdvancementTabAccessor

        val baseX = (screen.width - WINDOW_WIDTH) / 2
        val baseY = (screen.height - WINDOW_HEIGHT) / 2

        val relativeX = (click.x() - baseX).toInt()
        val relativeY = (click.y() - baseY).toInt()

        if (relativeX !in 1..<TREE_WIDTH || relativeY <= 0 || relativeY >= TREE_HEIGHT) {
            return false
        }

        val originX = MathHelper.floor(tabAccessor.pinnedAdvancements_getOriginX())
        val originY = MathHelper.floor(tabAccessor.pinnedAdvancements_getOriginY())

        val hoveredWidget = tabAccessor.pinnedAdvancements_getWidgets().values.firstOrNull { widget ->
            widget.shouldRender(originX, originY, relativeX, relativeY)
        } ?: return false

        val widgetAccessor = hoveredWidget as AdvancementWidgetAccessor
        val entry = widgetAccessor.pinnedAdvancements_getAdvancement().advancementEntry
        val id = entry.id().toString()

        val configManager = PinnedAdvancementsConfigManager
        val config = configManager.config
        if (config.pinnedAdvancement == id) {
            return true
        }

        config.pinnedAdvancement = id
        configManager.save()

        val client = MinecraftClient.getInstance()
        PinnedAdvancementTracker.refreshNow(client)

        val title = Advancement.getNameFromIdentity(entry)
        client.player?.sendMessage(Text.literal("Pinned advancement: ").append(title), true)

        return true
    }
}
