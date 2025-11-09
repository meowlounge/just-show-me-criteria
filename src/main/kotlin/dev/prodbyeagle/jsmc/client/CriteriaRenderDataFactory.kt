package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.client.font.TextRenderer
import java.util.Locale

class CriteriaRenderDataFactory(
    private val config: ShowMeCriteriaConfig,
    private val textRenderer: TextRenderer
) {

    fun build(entry: AdvancementEntry, progress: AdvancementProgress): CriteriaRenderData {
        val names = collectCriteria(entry)
        if (names.isEmpty()) {
            return emptyData()
        }

        val unfinished = names.mapNotNull { name ->
            if (progress.getCriterionProgress(name)?.isObtained == true) null else name
        }

        if (unfinished.isEmpty()) {
            return emptyData()
        }

        val labeled = unfinished
            .map { it to formatName(it) }
            .sortedBy { (_, label) -> label.lowercase(Locale.ROOT) }

        val maxVisible = config.criteriaMaxVisible.coerceIn(1, 20)
        val visible = labeled.take(maxVisible)
        val overflow = (unfinished.size - visible.size).coerceAtLeast(0)

        val lines = visible.flatMap { (_, label) ->
            val bullet = "- $label"
            textRenderer.wrapLines(Text.literal(bullet), TEXT_WRAP_WIDTH)
        }

        return CriteriaRenderData(lines, overflow, unfinished.size)
    }

    private fun collectCriteria(entry: AdvancementEntry): LinkedHashSet<String> {
        val ordered = linkedSetOf<String>()
        val requirements = entry.value().requirements()
        try {
            val groups = requirements.requirements()
            groups.forEach { group -> group.forEach { ordered.add(it) } }
        } catch (_: Throwable) {
            try {
                val field = requirements.javaClass.getDeclaredField("requirements")
                field.isAccessible = true
                val raw = field.get(requirements) as? Iterable<*>
                raw?.forEach { group ->
                    (group as? Iterable<*>)?.forEach { (it as? String)?.let(ordered::add) }
                }
            } catch (_: Throwable) {
                // best-effort
            }
        }
        return ordered
    }

    private fun formatName(raw: String): String {
        if (raw.isBlank()) return "Unnamed Criterion"
        val identifier = Identifier.tryParse(raw)
        val base = identifier?.path ?: raw
        val cleaned = base
            .replace(':', ' ')
            .replace('/', ' ')
            .replace('.', ' ')
            .replace('-', ' ')

        val parts = cleaned
            .split('_', ' ')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (parts.isEmpty()) return base

        return parts.joinToString(" ") { part ->
            part.lowercase(Locale.ROOT).replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.ROOT) else ch.toString()
            }
        }
    }

    private fun emptyData() = CriteriaRenderData(emptyList(), 0, 0)

    companion object {
        private const val TEXT_WRAP_WIDTH = 220
    }
}
