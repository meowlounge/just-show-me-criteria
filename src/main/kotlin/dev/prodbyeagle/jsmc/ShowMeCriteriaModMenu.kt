package dev.prodbyeagle.jsmc

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import kotlin.math.roundToInt

open class ShowMeCriteriaModMenu : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent ->
            ShowMeCriteriaConfigManager.reloadIfChanged()
            createConfigScreen(parent)
        }
    }

    private fun createConfigScreen(parent: Screen): Screen {
        val working = ShowMeCriteriaConfigManager.config.deepCopy()

        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("Just Show Me Criteria"))

        val entryBuilder = builder.entryBuilder()

        builder.setSavingRunnable {
            applyToManager(working)
            ShowMeCriteriaConfigManager.save()
        }

        val general = builder.getOrCreateCategory(Text.literal("General"))
        general.addEntry(
            entryBuilder.startBooleanToggle(Text.literal("Enabled"), working.enabled)
                .setDefaultValue(true)
                .setSaveConsumer { working.enabled = it }
                .build()
        )
        general.addEntry(
            entryBuilder.startStrField(Text.literal("Tracked Advancement ID"), working.pinnedAdvancement)
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
        general.addEntry(
            entryBuilder.startIntSlider(Text.literal("Max Visible Criteria"), working.criteriaMaxVisible, 1, 20)
                .setDefaultValue(6)
                .setSaveConsumer { working.criteriaMaxVisible = it.coerceIn(1, 20) }
                .build()
        )

        val hud = builder.getOrCreateCategory(Text.literal("HUD Placement"))
        hud.addEntry(
            entryBuilder.startEnumSelector(Text.literal("Anchor"), ShowMeCriteriaConfig.HudConfig.Anchor::class.java, working.hud.anchor)
                .setDefaultValue(ShowMeCriteriaConfig.HudConfig.Anchor.TOP_RIGHT)
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
        hud.addEntry(
            entryBuilder.startIntSlider(
                Text.literal("Scale"),
                (working.hud.scale * 100).toInt(),
                50,
                300
            )
                .setDefaultValue(100)
                .setTextGetter { value ->
                    Text.literal(String.format("%.2f×", value / 100.0))
                }
                .setSaveConsumer { value ->
                    working.hud = working.hud.copy(scale = (value / 100.0).coerceIn(0.5, 3.0))
                }
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
            entryBuilder.startStrField(Text.literal("Description Color (hex)"), working.style.descriptionColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(descriptionColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startStrField(Text.literal("Status Text Color (hex)"), working.style.progressTextColor.orEmpty())
                .setDefaultValue("")
                .setSaveConsumer { working.style = working.style.copy(progressTextColor = it.trim().takeIf { str -> str.isNotEmpty() }) }
                .build()
        )
        style.addEntry(
            entryBuilder.startIntSlider(
                Text.literal("Background Opacity"),
                (working.style.backgroundOpacity * 100).roundToInt(),
                0,
                100
            )
                .setDefaultValue((ShowMeCriteriaConfig.StyleConfig.DEFAULT_OPACITY * 100).roundToInt())
                .setTextGetter { value -> Text.literal("$value%") }
                .setSaveConsumer { value ->
                    working.style = working.style.copy(backgroundOpacity = (value / 100.0).coerceIn(0.0, 1.0))
                }
                .build()
        )

        return builder.build()
    }

    private fun applyToManager(working: ShowMeCriteriaConfig) {
        val config = ShowMeCriteriaConfigManager.config
        config.enabled = working.enabled
        config.pinnedAdvancement = working.pinnedAdvancement
        config.hideWhenCompleted = working.hideWhenCompleted
        config.criteriaMaxVisible = working.criteriaMaxVisible
        config.hud = working.hud.copy()
        config.style = working.style.copy()
    }

    private fun ShowMeCriteriaConfig.deepCopy(): ShowMeCriteriaConfig {
        return this.copy(
            hud = this.hud.copy(),
            style = this.style.copy()
        )
    }
}
