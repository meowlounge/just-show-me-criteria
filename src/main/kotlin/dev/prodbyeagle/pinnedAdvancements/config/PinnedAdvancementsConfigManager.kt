package dev.prodbyeagle.pinnedAdvancements.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object PinnedAdvancementsConfigManager {

    private val LOGGER = LoggerFactory.getLogger("PinnedAdvancements|Config")
    private val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    private val CONFIG_PATH: Path = FabricLoader.getInstance()
        .configDir
        .resolve("pinned-advancements.json")

    var config: PinnedAdvancementsConfig = PinnedAdvancementsConfig()
        private set

    private var lastModified: Long = 0L

    fun load() {
        try {
            Files.createDirectories(CONFIG_PATH.parent)

            if (Files.notExists(CONFIG_PATH)) {
                write(config)
                lastModified = Files.getLastModifiedTime(CONFIG_PATH).toMillis()
                return
            }

            Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8).use { reader ->
                config = GSON.fromJson(reader, PinnedAdvancementsConfig::class.java) ?: PinnedAdvancementsConfig()
            }
            lastModified = Files.getLastModifiedTime(CONFIG_PATH).toMillis()
        } catch (throwable: Throwable) {
            LOGGER.error("Failed to load config at {}", CONFIG_PATH.toAbsolutePath(), throwable)
            config = PinnedAdvancementsConfig()
        }
    }

    fun reloadIfChanged() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                val changedAt = Files.getLastModifiedTime(CONFIG_PATH).toMillis()
                if (changedAt > lastModified) {
                    load()
                }
            }
        } catch (throwable: Throwable) {
            LOGGER.warn("Failed to check config timestamp at {}", CONFIG_PATH.toAbsolutePath(), throwable)
        }
    }

    fun save() {
        write(config)
    }

    private fun write(current: PinnedAdvancementsConfig) {
        try {
            Files.createDirectories(CONFIG_PATH.parent)
            Files.newBufferedWriter(
                CONFIG_PATH,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            ).use { writer ->
                GSON.toJson(current, writer)
            }
            lastModified = Files.getLastModifiedTime(CONFIG_PATH).toMillis()
        } catch (throwable: Throwable) {
            LOGGER.error("Failed to save config at {}", CONFIG_PATH.toAbsolutePath(), throwable)
        }
    }
}
