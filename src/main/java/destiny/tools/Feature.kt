/**
 * Created by smallufo on 2021-08-06.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


interface Builder<Model> {

  fun build(): Model
}

interface Feature<out Config : Any, Model : Any?> : Serializable {

  val key: String

  val defaultConfig: Config

  data class GmtCacheKey<Config>(
    val gmtJulDay: GmtJulDay,
    val loc: ILocation,
    val config: Config)

  val gmtCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  fun getCachedModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model {
    return gmtCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, config)
      cache[cacheKey] ?: run {
        getModel(gmtJulDay, loc, config).also { model: Model ->
          cache.put(cacheKey, model)
        }
      }
    } ?: getModel(gmtJulDay, loc, config)
  }

  fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model

  data class LmtCacheKey<Config>(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val config: Config
  )

  val lmtCache: Cache<LmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  fun getCacheModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model {
    return lmtCache?.let { cache ->
      val cacheKey = LmtCacheKey(lmt, loc, config)
      cache[cacheKey]?.also {
        logger.trace { "cache hit" }
      } ?: run {
        logger.info { "cache miss" }
        getModel(lmt, loc, config)?.also { model: Model ->
          logger.info { "put ${model!!::class.simpleName}(${model.hashCode()}) into cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: getModel(lmt, loc, config)
  }

  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getCachedModel(gmtJulDay, loc, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}

