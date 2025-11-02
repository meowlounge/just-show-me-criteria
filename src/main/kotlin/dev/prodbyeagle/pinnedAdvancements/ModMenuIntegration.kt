package dev.prodbyeagle.pinnedAdvancements

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfig
import dev.prodbyeagle.pinnedAdvancements.config.PinnedAdvancementsConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent ->
            PinnedAdvancementsConfigManager.reloadIfChanged()
            createConfigScreen(parent)
        }
    }

    private fun createConfigScreen(parent: Screen): Screen {
        val working = PinnedAdvancementsConfigManager.config.deepCopy()

        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("Pinned Advancements"))

        val entryBuilder = builder.entryBuilder()

        builder.setSavingRunnable {
            applyToManager(working)
            PinnedAdvancementsConfigManager.save()
        }

        val general = builder.getOrCreateCategory(Text.literal("General"))
        general.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Enabled"), working.enabled)
                .setDefaultValue(true)
                .setSaveConsumer { working.enabled = it }
                .build()
        )
        general.addEntry(
            entryBuilder.startStrField(Text.literal("Pinned Advancement ID"), working.pinnedAdvancement)
                .setDefaultValue("minecraft:story/mine_diamond")
                .setSaveConsumer { working.pinnedAdvancement = it.trim().ifBlank { "minecraft:story/mine_diamond" } }
                .build()
        )
        general.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Hide When Completed"), working.hideWhenCompleted)
                .setDefaultValue(false)
                .setSaveConsumer { working.hideWhenCompleted = it }
                .build()
        )

        val display = builder.getOrCreateCategory(Text.literal("Display"))
        display.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Show Title"), working.showTitle)
                .setDefaultValue(true)
                .setSaveConsumer { working.showTitle = it }
                .build()
        )
        display.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Show Description"), working.showDescription)
                .setDefaultValue(true)
                .setSaveConsumer { working.showDescription = it }
                .build()
        )
        display.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Show Progress Bar"), working.showProgressBar)
                .setDefaultValue(true)
                .setSaveConsumer { working.showProgressBar = it }
                .build()
        )
        display.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Show Progress Text"), working.showProgressText)
                .setDefaultValue(true)
                .setSaveConsumer { working.showProgressText = it }
                .build()
        )
        display.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Show Icon"), working.showIcon)
                .setDefaultValue(true)
                .setSaveConsumer { working.showIcon = it }
                .build()
        )

        val customText = builder.getOrCreateCategory(Text.literal("Custom Text"))
        customText.addEntry(
            entryBuilder.startStrField(Text.literal("Custom Title"), working.customTitle ?: "")
                .setDefaultValue("")
                .setSaveConsumer { value ->
                    working.customTitle = value.trim().takeIf { it.isNotEmpty() }
                }
                .build()
        )
        customText.addEntry(
            entryBuilder.startStrList(Text.literal("Custom Description Lines"), working.customDescription?.toMutableList() ?: mutableListOf())
                .setDefaultValue(mutableListOf())
                .setSaveConsumer { list ->
                    val cleaned = list.map { it.trim() }.filter { it.isNotEmpty() }
                    working.customDescription = cleaned.takeIf { it.isNotEmpty() }
                }
                .build()
        )

        val customIcon = builder.getOrCreateCategory(Text.literal("Custom Icon"))
        customIcon.addEntry(
            entryBuilder.startStrField(Text.literal("Item ID"), working.customIcon?.item ?: "")
                .setDefaultValue("")
                .setSaveConsumer { value ->
                    val trimmed = value.trim()
                    if (trimmed.isEmpty()) {
                        working.customIcon = working.customIcon?.copy(item = null)
                        if (working.customIcon?.item == null) {
                            working.customIcon = null
                        }
                    } else {
                        val icon = working.customIcon ?: PinnedAdvancementsConfig.IconConfig()
                        icon.item = trimmed
                        working.customIcon = icon
                    }
                }
                .build()
        )
        customIcon.addEntry(
            entryBuilder.startIntField(Text.literal("Item Count"), working.customIcon?.count ?: 1)
                .setDefaultValue(1)
                .setMin(1)
                .setMax(64)
                .setSaveConsumer { value ->
                    val icon = working.customIcon ?: PinnedAdvancementsConfig.IconConfig()
                    icon.count = value.coerceIn(1, 64)
                    working.customIcon = icon
                }
                .build()
        )

        val hud = builder.getOrCreateCategory(Text.literal("HUD Placement"))
        hud.addEntry(
            entryBuilder.startEnumSelector(Text.literal("Anchor"), PinnedAdvancementsConfig.HudConfig.Anchor::class.java, working.hud.anchor)
                .setDefaultValue(PinnedAdvancementsConfig.HudConfig.Anchor.TOP_RIGHT)
                .setSaveConsumer { working.hud = working.hud.copy(anchor = it) }
                .build()
        )
        hud.addEntry(
            entryBuilder.startIntField(Text.literal("Offset X"), working.hud.offsetX)
                .setDefaultValue(16)
                .setSaveConsumer { working.hud = working.hud.copy(offsetX = it) }
                .build()
        )
        hud.addEntry(
            entryBuilder.startIntField(Text.literal("Offset Y"), working.hud.offsetY)
                .setDefaultValue(16)
                .setSaveConsumer { working.hud = working.hud.copy(offsetY = it) }
                .build()
        )

        val style = builder.getOrCreateCategory(Text.literal("Style"))
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Header Color (hex)"), working.style.headerColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(headerColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Title Color (hex)"), working.style.titleColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(titleColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Description Color (hex)"), working.style.descriptionColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(descriptionColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Progress Text Color (hex)"), working.style.progressTextColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(progressTextColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Progress Bar Color (hex)"), working.style.progressBarColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(progressBarColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Progress Bar Background (hex)"), working.style.progressBarBackgroundColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(progressBarBackgroundColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )

        return builder.build()
    }

    private fun applyToManager(working: PinnedAdvancementsConfig) {
        val config = PinnedAdvancementsConfigManager.config
        config.enabled = working.enabled
        config.pinnedAdvancement = working.pinnedAdvancement
        config.hideWhenCompleted = working.hideWhenCompleted
        config.showTitle = working.showTitle
        config.showDescription = working.showDescription
        config.showProgressBar = working.showProgressBar
        config.showProgressText = working.showProgressText
        config.showIcon = working.showIcon
        config.customTitle = working.customTitle
        config.customDescription = working.customDescription?.toList()
        config.customIcon = working.customIcon?.copy()
        config.hud = working.hud.copy()
        config.style = working.style.copy()
    }

    private fun PinnedAdvancementsConfig.deepCopy(): PinnedAdvancementsConfig {
        return this.copy(
            customDescription = this.customDescription?.toList(),
            customIcon = this.customIcon?.copy(),
            hud = this.hud.copy(),
            style = this.style.copy()
        )
    }
}
