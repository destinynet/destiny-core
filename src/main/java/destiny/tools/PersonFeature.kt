package destiny.tools

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache

interface PersonFeature<out Config : Any, Model> : Feature<Config, Model> {

  data class GmtCacheKey<Config>(
    val gmtJulDay: GmtJulDay,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: Config
  )

  val gmtPersonCache: Cache<GmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  fun getPersonCacheModel(gmtJulDay: GmtJulDay,
                          loc: ILocation,
                          gender: Gender,
                          name: String?,
                          place: String?,
                          config: @UnsafeVariance Config = defaultConfig): Model {
    return gmtPersonCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "cache hit" }
      }?: run {
        logger.info { "cache miss" }
        getPersonModel(gmtJulDay, loc, gender, name, place, config)?.also { model: Model ->
          logger.info { "put ${model!!::class.simpleName}(${model.hashCode()}) into cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: getPersonModel(gmtJulDay, loc, gender, name, place, config)
  }

  fun getPersonModel(gmtJulDay: GmtJulDay,
                     loc: ILocation,
                     gender: Gender,
                     name: String?,
                     place: String?,
                     config: @UnsafeVariance Config = defaultConfig): Model

  data class LmtCacheKey<Config>(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: Config
  )

  val lmtPersonCache: Cache<LmtCacheKey<@UnsafeVariance Config>, Model>?
    get() = null

  fun getPersonCacheModel(lmt: ChronoLocalDateTime<*>,
                          loc: ILocation,
                          gender: Gender,
                          name: String?,
                          place: String?,
                          config: @UnsafeVariance Config = defaultConfig) : Model {
    return lmtPersonCache?.let { cache ->
      val cacheKey = LmtCacheKey(lmt, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "cache hit" }
      }?: run {
        logger.info { "cache miss" }
        getPersonModel(lmt, loc, gender, name, place, config)?.also { model: Model ->
          logger.info { "put ${model!!::class.simpleName}(${model.hashCode()}) into cache" }
          cache.put(cacheKey, model)
        }
      }
    } ?: getPersonModel(lmt, loc, gender, name, place, config)
  }

  fun getPersonModel(lmt: ChronoLocalDateTime<*>,
                     loc: ILocation,
                     gender: Gender,
                     name: String?,
                     place: String?,
                     config: @UnsafeVariance Config = defaultConfig): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getPersonModel(gmtJulDay, loc, gender, name, place, config)
  }

  fun getPersonModel(bdnp: IBirthDataNamePlace, config: @UnsafeVariance Config = defaultConfig): Model {
    return getPersonModel(bdnp.gmtJulDay, bdnp.location, bdnp.gender, bdnp.name, bdnp.place, config)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model {
    return getPersonModel(gmtJulDay, loc, Gender.男, null, null, config)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
