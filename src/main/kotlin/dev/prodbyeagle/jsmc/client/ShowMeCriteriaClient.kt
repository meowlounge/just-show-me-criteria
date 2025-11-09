package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.client.command.ShowMeCriteriaCommands
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.util.Identifier

class ShowMeCriteriaClient : ClientModInitializer {

    override fun onInitializeClient() {
        ShowMeCriteriaConfigManager.load()
        ShowMeCriteriaCommands.register()

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            ShowMeCriteriaTracker.tick(client)
        })

        HudElementRegistry.addLast(
            Identifier.of("jsmc", "criteria_hud"),
            ShowMeCriteriaHudRenderer
        )
    }
}
