/**
 * Created by smallufo on 2021-09-18.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.cache.Cache


abstract class AbstractCachedFeature<out Config : Any, Model : Any?> : Feature<Config, Model> {

  enum class CacheGrain {
    SECOND,
    MINUTE,
    HOUR,
    DAY
  }

  data class GmtCacheKey<Config>(
    val gmtJulDay: GmtJulDay,
    val loc: ILocation,
    val config: Config
  )

  var lmtCacheGrain: CacheGrain? = null

  open val gmtCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model {
    return gmtCache?.let { cache ->
      // TODO cache grain
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

    val errorFixedLmt = lmt.fixError()

    return lmtCache?.let { cache ->

      val newLmt = lmtCacheGrain?.let { grain ->
        when (grain) {
          CacheGrain.SECOND -> errorFixedLmt.grainSecond()
          CacheGrain.MINUTE -> errorFixedLmt.grainMinute()
          CacheGrain.HOUR   -> errorFixedLmt.grainHour()
          CacheGrain.DAY    -> errorFixedLmt.grainDay()
        }
      } ?: errorFixedLmt


      val cacheKey = LmtCacheKey(newLmt, loc, config)
      cache[cacheKey]?.also {
        logger.trace { "LMT cache hit" }
      } ?: run {
        logger.trace { "LMT cache miss" }
        calculate(newLmt, loc, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into LMT cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: calculate(errorFixedLmt, loc, config)
  }

  open fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getModel(gmtJulDay, loc, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }

    fun ChronoLocalDateTime<*>.fixError(): ChronoLocalDateTime<out ChronoLocalDate> {
      return if (this.get(ChronoField.SECOND_OF_MINUTE) == 59 && this.get(ChronoField.NANO_OF_SECOND) > 999_990_000) {
        this.with(ChronoField.NANO_OF_SECOND, 0).plus(1, ChronoUnit.SECONDS)
      } else if (this.get(ChronoField.SECOND_OF_MINUTE) == 0 && this.get(ChronoField.NANO_OF_SECOND) < 10000) {
        this.with(ChronoField.NANO_OF_SECOND, 0)
      } else {
        this
      }
    }

    fun ChronoLocalDateTime<*>.grainSecond(): ChronoLocalDateTime<out ChronoLocalDate> {
      return this.with(ChronoField.MICRO_OF_SECOND, 0)
    }

    fun ChronoLocalDateTime<*>.grainMinute(): ChronoLocalDateTime<out ChronoLocalDate> {
      return this.grainSecond().with(ChronoField.SECOND_OF_MINUTE, 0)
    }

    fun ChronoLocalDateTime<*>.grainHour(): ChronoLocalDateTime<out ChronoLocalDate> {
      return this.grainMinute().with(ChronoField.MINUTE_OF_HOUR, 0)
    }

    fun ChronoLocalDateTime<*>.grainDay(): ChronoLocalDateTime<out ChronoLocalDate> {
      return this.grainHour().with(ChronoField.HOUR_OF_DAY, 0)
        .with(ChronoField.SECOND_OF_MINUTE, 1) // 額外加一秒，避免計算時 round off error
    }
  }
}
