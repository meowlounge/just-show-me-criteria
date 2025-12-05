package dev.prodbyeagle.jsmc.client.command

import net.minecraft.util.Identifier

internal object AdvancementFilters {
    private const val RECIPES_NAMESPACE = "recipes"
    private const val RECIPES_PATH = "recipes"
    private const val RECIPES_PREFIX = "$RECIPES_PATH/"

    fun isRecipeAdvancement(id: Identifier): Boolean {
        if (id.namespace == RECIPES_NAMESPACE) return true
        val path = id.path
        return path == RECIPES_PATH || path.startsWith(RECIPES_PREFIX)
    }
}
