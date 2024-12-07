/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IHourBranchFeature
import destiny.core.chinese.Branch
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.random.RandomService
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import javax.cache.Cache

@Serializable
data class LunarStationModernConfig(
  override val lunarStationConfig: LunarStationConfig = LunarStationConfig(),
  override var method: IModernContextModel.Method = IModernContextModel.Method.NOW,
  override var specifiedGmtJulDay: GmtJulDay? = null,
  override var description: String? = null): ILunarStationModernConfig, ILunarStationConfig by lunarStationConfig

context(ILunarStationConfig)
@DestinyMarker
class LunarStationModernConfigBuilder : Builder<LunarStationModernConfig> {

  var method: IModernContextModel.Method = IModernContextModel.Method.NOW
  var specifiedGmtJulDay: GmtJulDay? = null
  var description: String? = null

  override fun build(): LunarStationModernConfig {
    return LunarStationModernConfig(lunarStationConfig, method, specifiedGmtJulDay, description)
  }

  companion object {
    context(ILunarStationConfig)
    fun lunarStationModern(block: LunarStationModernConfigBuilder.() -> Unit = {}): LunarStationModernConfig {
      return LunarStationModernConfigBuilder().apply(block).build()
    }
  }
}

@Named
class LunarStationModernFeature(private val lunarStationFeature: ILunarStationFeature,
                                private val hourBranchFeature: IHourBranchFeature,
                                private val randomService: RandomService,
                                private val julDayResolver: JulDayResolver,
                                @Transient
                                private val lsModernCache : Cache<GmtCacheKey<*>, IModernContextModel>
) : AbstractCachedPersonFeature<LunarStationModernConfig, IModernContextModel>() {

  override val key: String = "lunarStationModern"

  override val defaultConfig: LunarStationModernConfig = LunarStationModernConfig()

  override val gmtPersonCache: Cache<GmtCacheKey<LunarStationModernConfig>, IModernContextModel>
    @Suppress("UNCHECKED_CAST")
    get() = lsModernCache as Cache<GmtCacheKey<LunarStationModernConfig>, IModernContextModel>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: LunarStationModernConfig): IModernContextModel {
    val created = LocalDateTime.now()

    val time = when(config.method) {
      IModernContextModel.Method.NOW -> created

      IModernContextModel.Method.RANDOM_HOUR -> hourBranchFeature.getDailyBranchMiddleMap(
        created.toLocalDate(),
        loc,
        config.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig
      )[randomService.randomEnum(Branch::class.java)]!!
      IModernContextModel.Method.SPECIFIED   -> {
        config.specifiedGmtJulDay?.let { TimeTools.getLmtFromGmt(it, loc, julDayResolver) }
          ?: throw IllegalArgumentException("specifiedTime is null")
      }
      IModernContextModel.Method.RANDOM_TIME -> randomService.getRandomTime(
        LocalDate.now()
          .minusYears(60), LocalDate.now()
      )
    }

    val contextModel: ContextModel = lunarStationFeature.getModel(time, loc, config.lunarStationConfig)
    val bd = BirthData(TimeLoc(time, loc), gender)
    val bdnp: IBirthDataNamePlace = BirthDataNamePlace(bd, name = null, place)
    return ModernContextModel(contextModel, bdnp, created, config.method, config.description)
  }


  companion object {
    const val CACHE_LUNAR_STATION_MODERN_FEATURE = "lsModernCache"
  }
}
