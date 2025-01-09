/**
 * Created by smallufo on 2024-12-14.
 */
package destiny.tools.config

import destiny.tools.KotlinLogging
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class Reloader<T : Any>(
  private val configDir: String = "config",
  private val configFilename: String,
  private val defaultConfigPath: String,
  private val parser: Parser<T>,
  private val validator: Validator<T>,
  private val holder: Holder<T>,
  private val reloadIntervalMs: Long = 60000,
) : AutoCloseable {

  private val executor = Executors.newSingleThreadScheduledExecutor { r ->
    Thread(r, "config-reloader").apply { isDaemon = true }
  }

  init {
    logger.info { "reloader init..." }
    initializeConfig()
    startReloadScheduler()
  }

  private fun initializeConfig() {
    val configDirectory = File(configDir)
    if (!configDirectory.exists()) {
      configDirectory.mkdirs()
    }

    val externalConfigFile = File(configDirectory, configFilename)
    logger.info { "checking external config file : ${externalConfigFile.absolutePath}" }
    if (!externalConfigFile.exists()) {
      val defaultConfigUrl = Thread.currentThread().contextClassLoader
        .getResource(defaultConfigPath)
        ?: throw IllegalStateException("Default config not found: $defaultConfigPath")

      logger.info { "Default config absolute path: ${defaultConfigUrl.path}" }

      // 從 classpath 複製默認配置
      defaultConfigUrl.openStream().use { input ->
        externalConfigFile.outputStream().use { output ->
          input.copyTo(output)
          logger.info { "defaultConfig file copied to $externalConfigFile" }
        }
      }
    }

    // 初始加载配置
    loadConfig()?.also { holder.updateConfig(it) }
  }

  private fun startReloadScheduler() {
    executor.scheduleAtFixedRate(
      { loadConfig()?.also { holder.updateConfig(it) } },
      reloadIntervalMs,
      reloadIntervalMs,
      TimeUnit.MILLISECONDS
    )
  }

  private fun loadConfig(): T? {
    val externalConfigFile = File(configDir, configFilename)

    return try {
      if (externalConfigFile.exists()) {
        val content = externalConfigFile.readText()
        logger.debug { "loaded external config file: ${externalConfigFile.absolutePath}" }
        holder.getConfig()
        parser.parse(content).takeIf { validator.validate(it) }
      } else {
        loadDefaultConfig()
      }
    } catch (e: Exception) {
      logger.error(e) { "Error loading config from ${externalConfigFile.absolutePath}, falling back to default" }
      loadDefaultConfig()
    }
  }

  private fun loadDefaultConfig(): T? {
    return try {
      val defaultConfigStream = Thread.currentThread().contextClassLoader
        .getResourceAsStream(defaultConfigPath)
        ?: throw IllegalStateException("Default config not found: $defaultConfigPath")

      logger.info { "loaded default config file: $defaultConfigPath" }
      val content = defaultConfigStream.bufferedReader().use { it.readText() }
      parser.parse(content).takeIf { validator.validate(it) }
    } catch (e: Exception) {
      logger.error(e) { "Error loading default config" }
      null
    }
  }

  override fun close() {
    executor.shutdown()
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow()
      }
    } catch (e: InterruptedException) {
      executor.shutdownNow()
      Thread.currentThread().interrupt()
    }
  }

  companion object {
    private val logger = KotlinLogging.logger {}
  }
}
