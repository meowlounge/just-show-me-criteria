package dev.prodbyeagle.jsmc.client.command

import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.prodbyeagle.jsmc.mixin.ClientAdvancementManagerAccessor
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource

internal object AdvancementSuggestions {
    val provider: SuggestionProvider<FabricClientCommandSource> = SuggestionProvider { context, builder ->
        val manager = context.source.client
            ?.networkHandler
            ?.advancementHandler
            ?.let { it as ClientAdvancementManagerAccessor }
            ?.`jsmc$getManager`()

        val ids = manager
            ?.advancements
            ?.map { it.advancementEntry.id().toString() }
            .orEmpty()

        CommandSource.suggestMatching(ids, builder)
    }
}
