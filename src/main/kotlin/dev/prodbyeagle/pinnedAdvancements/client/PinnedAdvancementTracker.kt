package dev.prodbyeagle.pinnedAdvancements.client

import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfig
import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfigManager
import dev.prodbyeagle.pinnedAdvancements.mixin.ClientAdvancementManagerAccessor
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object PinnedAdvancementTracker {

    private const val TEXT_WRAP_WIDTH = 125

    var display: PinnedAdvancementDisplay? = null
        private set

    var error: Text? = null
        private set

    fun tick(client: MinecraftClient) {
        PinnedAdvancementsConfigManager.reloadIfChanged()

        val config = PinnedAdvancementsConfigManager.config
        if (!config.enabled) {
            display = null
            error = null
            return
        }

        val player = client.player
        val networkHandler = client.networkHandler
        if (player == null || networkHandler == null) {
            display = null
            error = null
            return
        }

        val textRenderer = client.textRenderer
        val id = Identifier.tryParse(config.pinnedAdvancement)
        if (id == null) {
            display = null
            error = Text.literal("Invalid advancement id: ${config.pinnedAdvancement}")
            return
        }

        val advancementHandler = networkHandler.advancementHandler
        val entry = advancementHandler.get(id)
        if (entry == null) {
            display = null
            error = Text.literal("Advancement not found: $id")
            return
        }

        val accessor = advancementHandler as ClientAdvancementManagerAccessor
        val progressMap = accessor.pinnedAdvancements_progresses()
        val progress = progressMap[entry] ?: createEmptyProgress(entry)

        if (config.hideWhenCompleted && progress.isDone) {
            display = null
            error = null
            return
        }

        val advancement = entry.value()
        val displayData = advancement.display().orElse(null)

        val frame = displayData?.frame
        val frameText = frame?.toastText

        val titleLines = if (config.showTitle) {
            val titleText = resolveTitle(config, displayData, entry)
            if (titleText != null) textRenderer.wrapLines(titleText, TEXT_WRAP_WIDTH) else emptyList()
        } else {
            emptyList()
        }

        val descriptionLines = if (config.showDescription) {
            resolveDescription(config, displayData, textRenderer)
        } else {
            emptyList()
        }

        val progressText = if (config.showProgressText && progress.progressBarFraction != null) {
            progress.progressBarFraction?.asOrderedText()
        } else {
            null
        }

        val icon = if (config.showIcon) {
            resolveIcon(config.customIcon) ?: displayData?.icon?.copy()
        } else {
            null
        }

        val progressPercentage = progress.progressBarPercentage.coerceIn(0f, 1f)
        val isChallenge = frame == AdvancementFrame.CHALLENGE

        display = PinnedAdvancementDisplay(
            entry = entry,
            frame = frame,
            progress = progress,
            frameText = if (config.showTitle) frameText else null,
            titleLines = titleLines,
            descriptionLines = descriptionLines,
            progressText = progressText,
            icon = icon,
            progressPercentage = progressPercentage,
            complete = progress.isDone,
            isChallenge = isChallenge
        )
        error = null
    }

    fun refreshNow(client: MinecraftClient) {
        tick(client)
    }

    private fun resolveTitle(
        config: PinnedAdvancementsConfig,
        displayData: net.minecraft.advancement.AdvancementDisplay?,
        entry: AdvancementEntry
    ): Text? {
        val override = config.customTitle?.takeIf { it.isNotBlank() }?.let(Text::literal)
        return override ?: displayData?.title ?: Text.literal(entry.id().toString())
    }

    private fun resolveDescription(
        config: PinnedAdvancementsConfig,
        displayData: net.minecraft.advancement.AdvancementDisplay?,
        textRenderer: net.minecraft.client.font.TextRenderer
    ): List<OrderedText> {
        val customDescription = config.customDescription?.takeIf { it.isNotEmpty() }

        val sourceLines = when {
            customDescription != null -> customDescription.map(Text::literal)
            displayData != null       -> listOf(displayData.description)
            else                      -> emptyList()
        }

        if (sourceLines.isEmpty()) {
            return emptyList()
        }

        return sourceLines.flatMap { textRenderer.wrapLines(it, TEXT_WRAP_WIDTH) }
    }

    private fun resolveIcon(iconConfig: PinnedAdvancementsConfig.IconConfig?): ItemStack? {
        if (iconConfig == null) return null
        val itemId = iconConfig.item?.takeIf { it.isNotBlank() } ?: return null
        val identifier = Identifier.tryParse(itemId) ?: return null
        val item = Registries.ITEM.get(identifier)
        val count = iconConfig.count
        return ItemStack(item, count.coerceIn(1, 64))
    }

    private fun createEmptyProgress(entry: AdvancementEntry): AdvancementProgress {
        return AdvancementProgress().also {
            it.init(entry.value().requirements())
        }
    }
}
