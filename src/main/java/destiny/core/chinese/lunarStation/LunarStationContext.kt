package destiny.core.chinese.lunarStation

import destiny.core.*
import destiny.core.Scale.*
import destiny.core.astrology.LunarStation
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.Branch
import destiny.core.chinese.lunarStation.IModernContextModel.Method
import destiny.tools.random.RandomService
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime


/**
 * 二十八宿 禽星占卜 Context
 */
interface ILunarStationContext {

  val yearlyImpl: ILunarStationYearly
  val monthlyImpl: ILunarStationMonthly
  val dailyImpl: ILunarStationDaily
  val hourlyImpl: ILunarStationHourly

  val eightWordsImpl: IEightWordsFactory
  val monthAlgo: IFinalMonthNumber.MonthAlgo

  fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale> = listOf(YEAR, MONTH, DAY, HOUR)): Map<Scale, LunarStation>


  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation): IContextModel

  companion object {

    /**
     * 禽星鎖泊：
     *
     * 山水田園井，刀天草岸風；
     * 火月周流轉，七曜長生宮。
     * 日午月未上，水土俱申中；
     * 木亥火寅地，金生與巳同。
     * 彼禽算外圈，我禽算內圈。
     */
    fun startBranch(planet: Planet): Branch {
      return when (planet) {
        Planet.SUN                    -> Branch.午
        Planet.MOON                   -> Branch.未
        Planet.MARS                   -> Branch.寅
        Planet.MERCURY, Planet.SATURN -> Branch.申
        Planet.JUPITER                -> Branch.亥
        Planet.VENUS                  -> Branch.巳
        else                          -> throw IllegalArgumentException()
      }
    }

    /** 彼禽 */
    fun getOppoHouse(oppo: LunarStation, hourBranch: Branch): OppoHouse {
      return startBranch(oppo.planet).let { b ->
        val steps = hourBranch.getAheadOf(b)
        OppoHouse.山.next(steps)
      }
    }

    /** 彼禽 外圈 */
    val oppoHouseMap: Map<Branch, OppoHouse> by lazy {
      val startPair = Branch.子 to OppoHouse.湯火
      generateSequence(startPair) { (branch, oppoHouse) ->
        branch.next to oppoHouse.next
      }.take(12)
        .toMap()
    }

    /** 我禽 */
    fun getSelfHouse(self: LunarStation, hourBranch: Branch): SelfHouse {
      return startBranch(self.planet).let { b ->
        val steps = hourBranch.getAheadOf(b)
        SelfHouse.山.next(steps)
      }
    }

    /** 我禽 內圈 */
    val selfHouseMap: Map<Branch, SelfHouse> by lazy {
      val startPair = Branch.子 to SelfHouse.湖
      generateSequence(startPair) { (branch, selfHouse) ->
        branch.next to selfHouse.next
      }.take(12)
        .toMap()
    }
  }
}


/**
 * 禽星占卜 Context
 */
class LunarStationContext(override val yearlyImpl: ILunarStationYearly,
                          override val monthlyImpl: ILunarStationMonthly,
                          override val dailyImpl: ILunarStationDaily,
                          override val hourlyImpl: ILunarStationHourly,

                          override val eightWordsImpl: IEightWordsStandardFactory,
                          val chineseDateImpl: IChineseDate,
                          override val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS) : ILunarStationContext,
                                                                                                                                 IHiddenVenusFoe,
                                                                                                                                 Serializable {


  /** 暗金伏斷 */
  override fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>> {
    val impl = HiddenVenusFoeAnimalStar(
      yearlyImpl, monthlyImpl, dailyImpl, hourlyImpl, eightWordsImpl.yearMonthImpl, chineseDateImpl, eightWordsImpl.dayHourImpl, monthAlgo
    )
    return impl.getHiddenVenusFoe(lmt, loc)
  }

  override fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.associate { scale ->
      when (scale) {
        YEAR  -> YEAR to yearlyImpl.getYearly(lmt, loc)
        MONTH -> {
          val yearlyStation: LunarStation = yearlyImpl.getYearly(lmt, loc)
          val monthBranch = eightWordsImpl.yearMonthImpl.getMonth(lmt, loc).branch
          val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, eightWordsImpl.dayHourImpl)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, monthAlgo
          )
          MONTH to monthlyImpl.getMonthly(yearlyStation, monthNumber)
        }
        DAY   -> DAY to dailyImpl.getDaily(lmt, loc)
        HOUR  -> HOUR to hourlyImpl.getHourly(lmt, loc)
      }
    }
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation): IContextModel {
    val eightWords = eightWordsImpl.getEightWords(lmt, loc)
    val models = getScaleMap(lmt, loc)

    val dayIndex = dailyImpl.getDailyIndex(lmt, loc)
    val hourStation = hourlyImpl.getHourly(lmt, loc)
    val oppo = ILunarStationHourly.getOpponent(dayIndex, hourStation)
    val self = ILunarStationHourly.getSelf1(hourStation, eightWords.hour.branch)

    val oppoHouse = ILunarStationContext.getOppoHouse(oppo, eightWords.hour.branch)
    val selfHouse = ILunarStationContext.getSelfHouse(self, eightWords.hour.branch)

    val reversed = ILunarStationHourly.getReversed(dayIndex, hourStation)
    val hiddenVenusFoe: Set<Pair<Scale, Scale>> = getHiddenVenusFoe(lmt, loc)

    return ContextModel(
      eightWords,
      models[YEAR]!!,
      models[MONTH]!!,
      dayIndex.station(),
      hourStation,
      dayIndex,
      oppo,
      oppoHouse,
      self,
      selfHouse,
      reversed,
      hiddenVenusFoe
    )
  }


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationContext) return false

    if (yearlyImpl != other.yearlyImpl) return false
    if (monthlyImpl != other.monthlyImpl) return false
    if (dailyImpl != other.dailyImpl) return false
    if (hourlyImpl != other.hourlyImpl) return false
    if (eightWordsImpl != other.eightWordsImpl) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (monthAlgo != other.monthAlgo) return false

    return true
  }

  override fun hashCode(): Int {
    var result = yearlyImpl.hashCode()
    result = 31 * result + monthlyImpl.hashCode()
    result = 31 * result + dailyImpl.hashCode()
    result = 31 * result + hourlyImpl.hashCode()
    result = 31 * result + eightWordsImpl.hashCode()
    result = 31 * result + chineseDateImpl.hashCode()
    result = 31 * result + monthAlgo.hashCode()
    return result
  }

}

interface ILunarStationModernContext : ILunarStationContext {

  fun getModernModel(loc: ILocation,
                     place: String?,
                     gender: Gender,
                     method: Method,
                     specifiedTime: ChronoLocalDateTime<*>? = null,
                     description: String? = null): IModernContextModel

  fun getModernModel(bdnp: IBirthDataNamePlace): IModernContextModel {
    return getModernModel(bdnp.location, bdnp.place, bdnp.gender, Method.SPECIFIED, bdnp.time, bdnp.name)
  }
}


/**
 * 禽星占卜 Modern Context
 */
class LunarStationModernContext(val ctx: ILunarStationContext, val randomService: RandomService, val julDayResolver: JulDayResolver) :
  ILunarStationModernContext, ILunarStationContext by ctx, Serializable {

  override fun getModernModel(loc: ILocation,
                              place: String?,
                              gender: Gender,
                              method: Method,
                              specifiedTime: ChronoLocalDateTime<*>?,
                              description: String?): IModernContextModel {

    val created = LocalDateTime.now()
    val hourBranch = randomService.randomEnum(Branch::class.java)

    val time: ChronoLocalDateTime<out ChronoLocalDate> = specifiedTime ?: when (method) {
      Method.NOW         -> created
      Method.RANDOM_HOUR -> (ctx as LunarStationContext).eightWordsImpl.dayHourImpl.getDailyBranchMiddleMap(
        created.toLocalDate(), loc, julDayResolver
      )[hourBranch]!!
      Method.SPECIFIED   -> specifiedTime ?: throw IllegalArgumentException("specifiedTime is null ")
      Method.RANDOM_TIME -> randomService.getRandomTime(
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
