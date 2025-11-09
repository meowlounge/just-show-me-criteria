package dev.prodbyeagle.jsmc.client

import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.text.OrderedText

data class ShowMeCriteriaDisplay(
    val entry: AdvancementEntry,
    val frame: AdvancementFrame?,
    val progress: AdvancementProgress?,
    val criteria: CriteriaRenderData,
)

data class CriteriaRenderData(
    val lines: List<OrderedText>,
    val overflowCount: Int,
    val remainingCount: Int
)
