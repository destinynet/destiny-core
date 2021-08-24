/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.RiseTransFeature
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransConfigBuilder
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.亥
import destiny.core.chinese.Branch.子
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit


/** 時辰切割 */
@Serializable
data class HourBranchConfig(val hourImpl : HourImpl = HourImpl.TST,
                            val transConfig: TransConfig = TransConfig()) {
  enum class HourImpl {
    TST,
    LMT
  }
}

class HourBranchConfigBuilder : Builder<HourBranchConfig> {

  var hourImpl = HourBranchConfig.HourImpl.TST

  var transConfig: TransConfig = TransConfig()

  /** true solar time */
  fun tst(block: TransConfigBuilder.() -> Unit = {}) {
    transConfig = TransConfigBuilder.trans(block)
    hourImpl = HourBranchConfig.HourImpl.TST
  }

  override fun build(): HourBranchConfig {
    return HourBranchConfig(hourImpl, transConfig)
  }

  companion object {
    fun hourBranchConfig(block : HourBranchConfigBuilder.() -> Unit = {}) : HourBranchConfig {
      return HourBranchConfigBuilder().apply(block).build()
    }
  }
}


interface IHourBranchFeature : Feature<HourBranchConfig, Branch> {
  /**
   * 取得下一個地支的開始時刻
   */
  fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver, config: HourBranchConfig): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    val resultLmt: ChronoLocalDateTime<*> = getLmtNextStartOf(lmt, loc, eb, config)
    return TimeTools.getGmtJulDay(resultLmt, loc)
  }


  fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, config: HourBranchConfig): ChronoLocalDateTime<*>

  /**
   * 取得「前一個」此地支的開始時刻
   */
  fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver,config: HourBranchConfig): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    val resultLmt: ChronoLocalDateTime<*> = getLmtPrevStartOf(lmt, loc, eb, config)
    return TimeTools.getGmtJulDay(resultLmt, loc)
  }

  fun getLmtPrevStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, config: HourBranchConfig): ChronoLocalDateTime<*>


  /**
   * 從目前的時刻，取得下一個（或上一個）時辰中點的 LMT 時刻為何
   * @param next true = 下一個時辰 , false = 上一個時辰
   */
  fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean = true, config: HourBranchConfig): ChronoLocalDateTime<*>


  /**
   * accessory function , 傳回當地，一日內的時辰「開始」時刻
   */
  fun getDailyBranchStartMap(day: ChronoLocalDate, loc: ILocation, config: HourBranchConfig): Map<Branch, ChronoLocalDateTime<*>> {
    val lmtStart = day.atTime(LocalTime.MIDNIGHT)

    return Branch.values().map { b ->
      val lmt = if (b == 子) {
        getLmtNextStartOf(lmtStart.minus(2, ChronoUnit.HOURS), loc, b, config)
      } else {
        getLmtNextStartOf(lmtStart, loc, b, config)
      }
      b to lmt
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

  /**
   * accessory function , 傳回當地，一日內的時辰「中間」時刻
   */
  fun getDailyBranchMiddleMap(day: ChronoLocalDate, loc: ILocation, config: HourBranchConfig = HourBranchConfig()): Map<Branch, ChronoLocalDateTime<*>> {
    val startTimeMap = getDailyBranchStartMap(day, loc, config)

    return startTimeMap.map { (branch, startTime) ->
      val endTime = if (branch != 亥) {
        startTimeMap[branch.next]
      } else {
        val start: ChronoLocalDateTime<*> = startTimeMap[branch] ?: error("")
        getLmtNextStartOf(start, loc, 子, config)
      }
      branch to startTime.plus(Duration.between(startTime, endTime).dividedBy(2))
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

}


class HourBranchFeature(private val riseTransFeature: RiseTransFeature ,
                        private val hourBoundaryImplMap: Map<HourBranchConfig.HourImpl, IHourBoundary>,
                        val julDayResolver: JulDayResolver) : IHourBranchFeature {
  override val key: String = "hourBranch"

  override val defaultConfig: HourBranchConfig = HourBranchConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourBranchConfig): Branch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: HourBranchConfig): Branch {

    return when (config.hourImpl) {
      HourBranchConfig.HourImpl.TST -> Tst.getHourBranch(lmt, loc, riseTransFeature, config.transConfig)
      HourBranchConfig.HourImpl.LMT -> Lmt.getHourBranch(lmt)

    }
  }

  override fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, config: HourBranchConfig): ChronoLocalDateTime<*> {
    return hourBoundaryImplMap[config.hourImpl]!!.getLmtNextStartOf(lmt, loc, eb, config.transConfig)
  }

  override fun getLmtPrevStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, config: HourBranchConfig): ChronoLocalDateTime<*> {
    return hourBoundaryImplMap[config.hourImpl]!!.getLmtPrevStartOf(lmt, loc, eb, config.transConfig)
  }

  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean, config: HourBranchConfig): ChronoLocalDateTime<*> {
    return hourBoundaryImplMap[config.hourImpl]!!.getLmtNextMiddleOf(lmt, loc, next, config)
  }


}
