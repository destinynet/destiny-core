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

  interface IGmtCacheKey<Config> : Feature.IGmtCacheKey<Config> {
    val gender: Gender
    val name: String?
    val place: String?
  }

  class GmtCacheKey<Config>(
    override val gmtJulDay: GmtJulDay,
    override val loc: ILocation,
    override val gender: Gender,
    override val name: String?,
    override val place: String?,
    override val config: Config
  ) : IGmtCacheKey<Config>


  fun getPersonCacheModel(gmtJulDay: GmtJulDay,
                          loc: ILocation,
                          gender: Gender,
                          name: String?,
                          place: String?,
                          config: @UnsafeVariance Config = defaultConfig): Model {
    return gmtCache?.let { cache ->
      val cacheKey = GmtCacheKey(gmtJulDay, loc, gender, name, place, config)
      cache[cacheKey]?.also {
        logger.trace { "cache hit" }
      }?: run {
        logger.trace { "cache miss" }
        getPersonModel(gmtJulDay, loc, gender, name, place, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into cache" }
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

  interface ILmtCacheKey<Config> : Feature.ILmtCacheKey<Config> {
    val gender: Gender
    val name: String?
    val place: String?
  }

  data class LmtCacheKey<Config>(
    override val lmt: ChronoLocalDateTime<*>,
    override val loc: ILocation,
    override val gender: Gender,
    override val name: String?,
    override val place: String?,
    override val config: Config
  ) : ILmtCacheKey<Config>

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
        logger.trace { "cache miss" }
        getPersonModel(lmt, loc, gender, name, place, config)?.also { model: Model ->
          logger.trace { "put ${model!!::class.simpleName}(${model.hashCode()}) into cache" }
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
