package dev.prodbyeagle.jsmc.client.command

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.argument.IdentifierArgumentType

object ShowMeCriteriaCommands {

    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("jsmc")
                    .then(
                        literal("select")
                            .then(
                                argument("advancement", IdentifierArgumentType.identifier())
                                    .suggests(AdvancementSuggestions.provider)
                                    .executes { ctx ->
                                        val id = ctx.getArgument("advancement", net.minecraft.util.Identifier::class.java)
                                        AdvancementSelection.select(ctx.source, id)
                                    }
                            )
                    )
            )
        }
    }
}
