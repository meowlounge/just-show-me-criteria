package dev.prodbyeagle.jsmc.client.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.prodbyeagle.jsmc.client.ShowMeCriteriaTracker
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.advancement.Advancement
import net.minecraft.text.Text
import net.minecraft.util.Identifier

internal object AdvancementSelection {
    private val ERROR_NO_CLIENT = Text.literal("Client unavailable.")
    private val ERROR_NOT_READY = Text.literal("No world or connection is active.")

    fun select(source: FabricClientCommandSource, id: Identifier): Int {
        val client = source.client ?: throw SimpleCommandExceptionType(ERROR_NO_CLIENT).create()
        val handler = client.networkHandler ?: throw SimpleCommandExceptionType(ERROR_NOT_READY).create()

        val entry = handler.advancementHandler.get(id)
            ?: throw SimpleCommandExceptionType(Text.literal("Advancement not found: $id")).create()

        val config = ShowMeCriteriaConfigManager.config
        val idString = id.toString()
        if (config.pinnedAdvancement == idString) {
            source.sendFeedback(Text.literal("Already tracking $idString"))
            return Command.SINGLE_SUCCESS
        }

        config.pinnedAdvancement = idString
        ShowMeCriteriaConfigManager.save()
        ShowMeCriteriaTracker.refreshNow(client)

        val title = Advancement.getNameFromIdentity(entry)
        source.sendFeedback(Text.literal("Tracking criteria for: ").append(title))
        return Command.SINGLE_SUCCESS
    }
}
