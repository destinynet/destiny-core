/**
 * Created by smallufo on 2021-09-18.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


abstract class AbstractCachedFeature<out Config : Any, Model : Any?> : Feature<Config, Model> {

  data class GmtCacheKey<Config>(
    val gmtJulDay: GmtJulDay,
    val loc: ILocation,
    val config: Config
  )

  open val gmtCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model {
    return gmtCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, config)
      cache[cacheKey]?.also {
        logger.trace { "GMT cache hit" }
      } ?: run {
        logger.trace { "GMT cache miss" }
        calculate(gmtJulDay, loc, config).also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into GMT cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: calculate(gmtJulDay, loc, config)
  }

  abstract fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model


  data class LmtCacheKey<Config>(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val config: Config
  )

  open val lmtCache: Cache<LmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config): Model {
    return lmtCache?.let { cache ->
      val cacheKey = LmtCacheKey(lmt, loc, config)
      cache[cacheKey]?.also {
        logger.trace { "LMT cache hit" }
      } ?: run {
        logger.trace { "LMT cache miss" }
        calculate(lmt, loc, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into LMT cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: calculate(lmt, loc, config)
  }

  open fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getModel(gmtJulDay, loc, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
