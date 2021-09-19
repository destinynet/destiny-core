/**
 * Created by smallufo on 2021-09-18.
 */
package destiny.tools

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


abstract class AbstractCachedPersonFeature<out Config : Any, Model> : PersonFeature<Config, Model> {

  data class GmtCacheKey<Config>(
    val gmtJulDay: GmtJulDay,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: Config
  )

  open val gmtPersonCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getPersonModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model {
    return gmtPersonCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.info { "GMT cache hit" }
      }?: run {
        logger.info { "GMT cache miss" }
        calculate(gmtJulDay, loc, gender, name, place, config)?.also { model: Model ->
          logger.info { "put ${model!!::class.simpleName}(${model.hashCode()}) into GMT cache." }
          cache.put(cacheKey, model)
        }
      }
    } ?: calculate(gmtJulDay, loc, gender, name, place, config)
  }

  abstract fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model

  data class LmtCacheKey<Config>(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: Config
  )

  open val lmtPersonCache: Cache<LmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getPersonModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model {
    return lmtPersonCache?.let { cache ->
      val cacheKey = LmtCacheKey(lmt, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "LMT cache hit" }
      }?: run {
        logger.trace { "LMT cache miss" }
        calculate(lmt, loc, gender, name, place, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into LMT cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: calculate(lmt, loc, gender, name, place, config)
  }

  open fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String? ,place: String? ,config: @UnsafeVariance Config) : Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return calculate(gmtJulDay, loc, gender, name, place, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
