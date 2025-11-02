package dev.prodbyeagle.pinnedAdvancements.client

import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

data class PinnedAdvancementDisplay(
    val entry: AdvancementEntry,
    val frame: AdvancementFrame?,
    val progress: AdvancementProgress?,
    val frameText: Text?,
    val titleLines: List<OrderedText>,
    val descriptionLines: List<OrderedText>,
    val progressText: OrderedText?,
    val icon: ItemStack?,
    val progressPercentage: Float,
    val complete: Boolean,
    val isChallenge: Boolean
)
