package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import dev.prodbyeagle.jsmc.mixin.ClientAdvancementManagerAccessor
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Util

object ShowMeCriteriaTracker {

    var display: ShowMeCriteriaDisplay? = null
        private set

    var error: Text? = null
        private set

    fun tick(client: MinecraftClient) {
        maybeReloadConfig()
        val config = ShowMeCriteriaConfigManager.config
        if (!config.enabled) {
            reset()
            return
        }

        val player = client.player
        val networkHandler = client.networkHandler
        if (player == null || networkHandler == null) {
            reset()
            return
        }

        val id = Identifier.tryParse(config.pinnedAdvancement)
        if (id == null) {
            setError("Invalid advancement id: ${config.pinnedAdvancement}")
            return
        }

        val advancementHandler = networkHandler.advancementHandler
        val accessor = advancementHandler as ClientAdvancementManagerAccessor
        val entry = advancementHandler.get(id)
        if (entry == null) {
            setError("Advancement not found: $id")
            return
        }

        val progress = accessor.`jsmc$getAdvancementProgresses`()[entry] ?: createEmptyProgress(entry)
        if (config.hideWhenCompleted && progress.isDone) {
            reset()
            return
        }

        val textRenderer = client.textRenderer
        val factory = getFactory(textRenderer)
        val frame = entry.value().display().orElse(null)?.frame

        display = ShowMeCriteriaDisplay(
            entry = entry,
            frame = frame,
            progress = progress,
            criteria = factory.build(config, entry, progress),
        )
        error = null
    }

    fun refreshNow(client: MinecraftClient) = tick(client)

    private fun reset() {
        display = null
        error = null
    }

    private fun setError(message: String) {
        display = null
        error = Text.literal(message)
    }

    private fun createEmptyProgress(entry: AdvancementEntry): AdvancementProgress {
        return AdvancementProgress().also { it.init(entry.value().requirements()) }
    }

    private fun maybeReloadConfig() {
        val now = Util.getMeasuringTimeMs()
        if (now < nextConfigPollAt) return
        ShowMeCriteriaConfigManager.reloadIfChanged()
        nextConfigPollAt = now + CONFIG_POLL_INTERVAL_MS
    }

    private fun getFactory(textRenderer: TextRenderer): CriteriaRenderDataFactory {
        val current = cachedFactory
        if (current == null || cachedTextRenderer !== textRenderer) {
            cachedTextRenderer = textRenderer
            cachedFactory = CriteriaRenderDataFactory(textRenderer)
        }
        return cachedFactory!!
    }

    private const val CONFIG_POLL_INTERVAL_MS = 1_000L
    private var nextConfigPollAt = 0L
    private var cachedFactory: CriteriaRenderDataFactory? = null
    private var cachedTextRenderer: TextRenderer? = null
}
