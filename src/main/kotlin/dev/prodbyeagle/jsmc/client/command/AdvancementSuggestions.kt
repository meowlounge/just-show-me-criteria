package dev.prodbyeagle.jsmc.client.command

import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.prodbyeagle.jsmc.mixin.ClientAdvancementManagerAccessor
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.advancement.AdvancementManager
import net.minecraft.command.CommandSource

internal object AdvancementSuggestions {
    private var cachedManager: AdvancementManager? = null
    private var cachedAdvancementSize: Int = -1
    private var cachedIds: List<String> = emptyList()

    val provider: SuggestionProvider<FabricClientCommandSource> = SuggestionProvider { context, builder ->
        val manager = context.source.client
            ?.networkHandler
            ?.advancementHandler
            ?.let { it as ClientAdvancementManagerAccessor }
            ?.`jsmc$getManager`()

        if (manager == null) {
            return@SuggestionProvider builder.buildFuture()
        }

        refreshCache(manager)
        return@SuggestionProvider CommandSource.suggestMatching(cachedIds, builder)
    }

    private fun refreshCache(manager: AdvancementManager) {
        val advancementSize = manager.advancements.size
        if (manager === cachedManager && advancementSize == cachedAdvancementSize) {
            return
        }
        cachedManager = manager
        cachedAdvancementSize = advancementSize
        cachedIds = manager.advancements.map { it.advancementEntry.id().toString() }
    }
}
