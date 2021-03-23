package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.Scale.*
import destiny.core.astrology.LunarStation
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 二十八宿 Context
 */
interface ILunarStationContext {

  val yearlyImpl: ILunarStationYearly
  val monthlyImpl: ILunarStationMonthly
  val dailyImpl: ILunarStationDaily
  val hourlyImpl: ILunarStationHourly

  fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                scales: List<Scale> = listOf(YEAR, MONTH, DAY, HOUR)): Map<Scale, LunarStation>


  fun getModel(lmt: ChronoLocalDateTime<*> , loc: ILocation) : ILunarStationContextModel

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
    private fun startBranch(planet: Planet) : Branch {
      return when(planet) {
        Planet.SUN -> Branch.午
        Planet.MOON -> Branch.未
        Planet.MARS -> Branch.寅
        Planet.MERCURY , Planet.SATURN -> Branch.申
        Planet.JUPITER -> Branch.亥
        Planet.VENUS -> Branch.巳
        else -> throw IllegalArgumentException()
      }
    }

    /** 彼禽 外圈 */
    fun getOppoHouseMap(oppo : LunarStation): Map<Branch, OppoHouse> {
      return generateSequence(startBranch(oppo.planet) to OppoHouse.山) { (branch , oppoHouse) ->
        branch.next to oppoHouse.next
      }.take(12)
        .toMap()
    }

    /** 我禽 內圈 */
    fun getSelfHouseMap(self : LunarStation) : Map<Branch , SelfHouse> {
      return generateSequence(startBranch(self.planet) to SelfHouse.山) { (branch , selfHouse) ->
        branch.next to selfHouse.next
      }.take(12)
        .toMap()
    }
  }
}


class LunarStationContext(override val yearlyImpl: ILunarStationYearly,
                          override val monthlyImpl: ILunarStationMonthly,
                          override val dailyImpl: ILunarStationDaily,
                          override val hourlyImpl: ILunarStationHourly,

                          val eightWordsImpl : IEightWordsStandardFactory,
                          private val chineseDateImpl: IChineseDate,
                          val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS
) : ILunarStationContext, Serializable {

  override fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.map { scale ->
      when (scale) {
        YEAR -> YEAR to yearlyImpl.getYearly(lmt, loc)
        MONTH -> {
          val yearlyStation: LunarStation = yearlyImpl.getYearly(lmt, loc)
          val monthBranch = eightWordsImpl.yearMonthImpl.getMonth(lmt, loc).branch
          val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, eightWordsImpl.dayHourImpl)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month,
            chineseDate.leapMonth,
            monthBranch,
            chineseDate.day,
            monthAlgo
          )
          MONTH to monthlyImpl.getMonthly(yearlyStation, monthNumber)
        }
        DAY -> DAY to dailyImpl.getDaily(lmt, loc)
        HOUR -> HOUR to hourlyImpl.getHourly(lmt, loc)
      }
    }.toMap()
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation): ILunarStationContextModel {
    val eightWords = eightWordsImpl.getEightWords(lmt, loc)
    val models = getModels(lmt, loc)

    val dayIndex = dailyImpl.getDailyIndex(lmt, loc)
    val hourStation = hourlyImpl.getHourly(lmt, loc)
    val oppo = ILunarStationHourly.getOpponent(dayIndex, hourStation)
    val self = ILunarStationHourly.getSelf1(hourStation, eightWords.hour.branch)

    val oppoHouseMap = ILunarStationContext.getOppoHouseMap(oppo)
    val selfHouseMap = ILunarStationContext.getSelfHouseMap(self)


    return ContextModel(eightWords ,
      models[YEAR]!! , models[MONTH]!! , dayIndex.station() , hourStation ,
      oppo , self , oppoHouseMap , selfHouseMap , emptySet()
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
