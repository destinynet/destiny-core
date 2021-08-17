/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime


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

class HourBranchFeature(private val riseTransFeature: RiseTransFeature ,
                        val julDayResolver: JulDayResolver) : Feature<HourBranchConfig , Branch>{
  override val key: String = "hourBranch"

  override val defaultConfig: HourBranchConfig = HourBranchConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourBranchConfig): Branch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: HourBranchConfig): Branch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return when (config.hourImpl) {
      HourBranchConfig.HourImpl.TST -> {
        /** 真太陽時 的時辰 */
        val nextMeridian = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN , TransPoint.MERIDIAN , config.transConfig))!!
        val nextNadir = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN , TransPoint.NADIR , config.transConfig))!!


        if (nextNadir > nextMeridian) {
          //子正到午正（上半天）
          val thirteenHoursAgo = gmtJulDay - 13 / 24.0

          val previousNadirGmt = riseTransFeature.getModel(thirteenHoursAgo, loc, RiseTransConfig(Planet.SUN , TransPoint.NADIR , config.transConfig))!!

          logger.debug("gmtJulDay = {}", gmtJulDay)

          val diffDays = nextMeridian - previousNadirGmt // 從子正到午正，總共幾秒
          val oneUnitDays = diffDays / 12.0
          logger.debug("diffDays = {} , oneUnitDays = {}", diffDays, oneUnitDays)
          when {
            gmtJulDay < previousNadirGmt + oneUnitDays      -> Branch.子
            gmtJulDay < previousNadirGmt + oneUnitDays * 3  -> Branch.丑
            gmtJulDay < previousNadirGmt + oneUnitDays * 5  -> Branch.寅
            gmtJulDay < previousNadirGmt + oneUnitDays * 7  -> Branch.卯
            gmtJulDay < previousNadirGmt + oneUnitDays * 9  -> Branch.辰
            gmtJulDay < previousNadirGmt + oneUnitDays * 11 -> Branch.巳
            else                                            -> Branch.午
          }
        } else {
          //午正到子正（下半天）
          val thirteenHoursAgo = gmtJulDay - 13 / 24.0

          val previousMeridian = riseTransFeature.getModel(thirteenHoursAgo, loc, RiseTransConfig(Planet.SUN , TransPoint.MERIDIAN , config.transConfig))!!

          val diffDays = nextNadir - previousMeridian
          val oneUnitDays = diffDays / 12.0

          when {
            gmtJulDay < previousMeridian + oneUnitDays      -> Branch.午
            gmtJulDay < previousMeridian + oneUnitDays * 3  -> Branch.未
            gmtJulDay < previousMeridian + oneUnitDays * 5  -> Branch.申
            gmtJulDay < previousMeridian + oneUnitDays * 7  -> Branch.酉
            gmtJulDay < previousMeridian + oneUnitDays * 9  -> Branch.戌
            gmtJulDay < previousMeridian + oneUnitDays * 11 -> Branch.亥
            else                                            -> Branch.子
          }
        }
      }
      HourBranchConfig.HourImpl.LMT -> Lmt.getHourBranch(lmt)

    }
  }
}
