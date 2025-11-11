package dev.prodbyeagle.jsmc.client

import dev.prodbyeagle.jsmc.config.ShowMeCriteriaConfig
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.reflect.Field
import java.util.Locale

class CriteriaRenderDataFactory(
    private val textRenderer: TextRenderer
) {

    private val cachedCriteria = mutableMapOf<Identifier, CachedCriteria>()

    fun build(
        config: ShowMeCriteriaConfig,
        entry: AdvancementEntry,
        progress: AdvancementProgress
    ): CriteriaRenderData {
        val cached = cachedCriteria.getOrPut(entry.id()) { cacheCriteria(entry) }
        if (cached.orderedNames.isEmpty()) return emptyData()

        val unfinished = cached.orderedNames.mapNotNull { name ->
            if (progress.getCriterionProgress(name)?.isObtained == true) null else name
        }

        if (unfinished.isEmpty()) return emptyData()

        val maxVisible = config.criteriaMaxVisible.coerceIn(1, 20)
        val visible = unfinished.take(maxVisible)
        val overflow = (unfinished.size - visible.size).coerceAtLeast(0)

        val lines = mutableListOf<OrderedText>()
        visible.forEach { name ->
            val visual = cached.visuals[name] ?: cacheMissingVisual(name)?.also {
                cached.visuals[name] = it
            }
            if (visual != null) {
                lines.addAll(visual.lines)
            }
        }

        return CriteriaRenderData(lines, overflow, unfinished.size)
    }

    private fun cacheCriteria(entry: AdvancementEntry): CachedCriteria {
        val collected = collectCriteria(entry)
        if (collected.isEmpty()) return CachedCriteria(emptyList(), mutableMapOf())

        val labeled = collected.map { it to formatName(it) }
            .sortedBy { (_, label) -> label.lowercase(Locale.ROOT) }

        val visuals = labeled.associateTo(mutableMapOf()) { (name, label) ->
            name to CriterionVisual(label, wrapLines(label))
        }

        val ordered = labeled.map { it.first }
        return CachedCriteria(ordered, visuals)
    }

    private fun cacheMissingVisual(name: String): CriterionVisual? {
        if (name.isBlank()) return null
        val label = formatName(name)
        return CriterionVisual(label, wrapLines(label))
    }

    private fun wrapLines(label: String): List<OrderedText> =
        textRenderer.wrapLines(Text.literal("- $label"), TEXT_WRAP_WIDTH)

    private fun collectCriteria(entry: AdvancementEntry): LinkedHashSet<String> {
        val ordered = linkedSetOf<String>()
        val requirements = entry.value().requirements()
        val direct = runCatching { requirements.requirements() }.getOrNull()
        if (direct != null) {
            direct.forEach { group -> group.forEach { ordered.add(it) } }
            return ordered
        }

        val field = resolveRequirementsField(requirements) ?: return ordered
        val raw = runCatching { field.get(requirements) as? Iterable<*> }.getOrNull()
        raw?.forEach { group ->
            (group as? Iterable<*>)?.forEach { (it as? String)?.let(ordered::add) }
        }
        return ordered
    }

    private fun resolveRequirementsField(instance: Any): Field? {
        var field = cachedRequirementsField
        if (field != null) return field
        field = runCatching {
            instance.javaClass.getDeclaredField("requirements").apply { isAccessible = true }
        }.getOrNull()
        cachedRequirementsField = field
        return field
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

    private data class CachedCriteria(
        val orderedNames: List<String>,
        val visuals: MutableMap<String, CriterionVisual>
    )

    private data class CriterionVisual(
        val label: String,
        val lines: List<OrderedText>
    )

    companion object {
        private const val TEXT_WRAP_WIDTH = 220
        @Volatile
        private var cachedRequirementsField: Field? = null
    }
}
