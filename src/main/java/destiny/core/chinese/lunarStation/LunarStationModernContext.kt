package destiny.core.chinese.lunarStation

import destiny.core.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.chinese.Branch
import destiny.tools.random.RandomService
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime

/**
 * deprecated for [LunarStationModernFeature]
 */
@Deprecated("LunarStationModernFeature")
interface ILunarStationModernContext : ILunarStationContext {

  fun getModernModel(loc: ILocation,
                     place: String?,
                     gender: Gender,
                     method: IModernContextModel.Method,
                     specifiedTime: ChronoLocalDateTime<*>? = null,
                     description: String? = null): IModernContextModel

  fun getModernModel(bdnp: IBirthDataNamePlace): IModernContextModel {
    return getModernModel(bdnp.location, bdnp.place, bdnp.gender, IModernContextModel.Method.SPECIFIED, bdnp.time, bdnp.name)
  }
}

/**
 * 禽星占卜 Modern Context
 * deprecated for [LunarStationModernFeature]
 */
@Deprecated("LunarStationModernFeature")
class LunarStationModernContext(val ctx: ILunarStationContext, val randomService: RandomService, val julDayResolver: JulDayResolver) :
  ILunarStationModernContext, ILunarStationContext by ctx, Serializable {

  override fun getModernModel(loc: ILocation,
                              place: String?,
                              gender: Gender,
                              method: IModernContextModel.Method,
                              specifiedTime: ChronoLocalDateTime<*>?,
                              description: String?): IModernContextModel {

    val created = LocalDateTime.now()
    val hourBranch = randomService.randomEnum(Branch::class.java)

    val time: ChronoLocalDateTime<out ChronoLocalDate> = specifiedTime ?: when (method) {
      IModernContextModel.Method.NOW         -> created
      IModernContextModel.Method.RANDOM_HOUR -> (ctx as LunarStationContext).eightWordsImpl.dayHourImpl.getDailyBranchMiddleMap(
        created.toLocalDate(), loc, julDayResolver, HourBranchConfig()
      )[hourBranch]!!
      IModernContextModel.Method.SPECIFIED   -> specifiedTime ?: throw IllegalArgumentException("specifiedTime is null ")
      IModernContextModel.Method.RANDOM_TIME -> randomService.getRandomTime(
        LocalDate.now()
          .minusYears(60), LocalDate.now()
      )

    }

    val contextModel: IContextModel = ctx.getModel(time, loc)

    val bd = BirthData(TimeLoc(time, loc), gender)
    val bdnp: IBirthDataNamePlace = BirthDataNamePlace(bd, name = null, place)

    return ModernContextModel(contextModel, bdnp, created, method, description)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationModernContext) return false

    if (ctx != other.ctx) return false
    if (randomService != other.randomService) return false
    if (julDayResolver != other.julDayResolver) return false

    return true
  }

  override fun hashCode(): Int {
    var result = ctx.hashCode()
    result = 31 * result + randomService.hashCode()
    result = 31 * result + julDayResolver.hashCode()
    return result
  }


}
