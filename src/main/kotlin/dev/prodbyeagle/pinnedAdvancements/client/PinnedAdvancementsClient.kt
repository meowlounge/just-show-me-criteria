package dev.prodbyeagle.pinnedAdvancements.client

import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.util.Identifier

class PinnedAdvancementsClient : ClientModInitializer {

    override fun onInitializeClient() {
        PinnedAdvancementsConfigManager.load()

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            PinnedAdvancementTracker.tick(client)
        })

        HudElementRegistry.addLast(
            Identifier.of("pinned-advancements", "pinned_advancement"),
            PinnedAdvancementHudRenderer
        )
    }
}
