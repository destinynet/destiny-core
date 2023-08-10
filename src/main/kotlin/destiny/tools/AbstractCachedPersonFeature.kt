/**
 * Created by smallufo on 2021-09-18.
 */
package destiny.tools

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.fixError
import destiny.tools.AbstractCachedFeature.Companion.grainDay
import destiny.tools.AbstractCachedFeature.Companion.grainHour
import destiny.tools.AbstractCachedFeature.Companion.grainMinute
import destiny.tools.AbstractCachedFeature.Companion.grainSecond
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
  ) : java.io.Serializable

  open val gmtPersonCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getPersonModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model {
    return gmtPersonCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "GMT cache hit" }
      }?: run {
        logger.trace { "GMT cache miss" }
        calculate(gmtJulDay, loc, gender, name, place, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into GMT cache." }
          cache.put(cacheKey, model)
        }
      }
    } ?: run {
      logger.trace { "${javaClass.simpleName} : No gmtPersonCache" }
      calculate(gmtJulDay, loc, gender, name, place, config)
    }
  }

  abstract fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model

  data class LmtCacheKey<Config>(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: Config
  ) : java.io.Serializable

  open var lmtCacheGrain: CacheGrain? = null

  open val lmtPersonCache: Cache<LmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getPersonModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config): Model {

    val errorFixedLmt = lmt.fixError()

    return lmtPersonCache?.let { cache ->

      val newLmt = lmtCacheGrain?.let { grain ->
        when (grain) {
          CacheGrain.SECOND -> errorFixedLmt.grainSecond()
          CacheGrain.MINUTE -> errorFixedLmt.grainMinute()
          CacheGrain.HOUR   -> errorFixedLmt.grainHour()
          CacheGrain.DAY    -> errorFixedLmt.grainDay()
        }
      } ?: errorFixedLmt

      val cacheKey = LmtCacheKey(newLmt, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "LMT cache hit" }
      }?: run {
        logger.trace { "LMT cache miss" }
        calculate(newLmt, loc, gender, name, place, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into LMT cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: run {
      logger.trace { "${javaClass.simpleName} : No lmtPersonCache" }
      calculate(errorFixedLmt, loc, gender, name, place, config)
    }
  }

  open fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String? ,place: String? ,config: @UnsafeVariance Config) : Model {
    return getPersonModel(lmt.toGmtJulDay(loc), loc, gender, name, place, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
