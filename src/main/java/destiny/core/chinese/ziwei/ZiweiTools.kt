/**
 * Created by smallufo on 2017-05-08.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import java.io.Serializable
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate

/**
 * 紫微斗數 雜項工具
 */
class ZiweiTools : Serializable {
  companion object {
    /** 列出此大限中，包含哪十個流年 (陰曆 cycle + 地支干支) , 並且「虛歲」各別是幾歲 ,   */
    fun getYearsOfFlowBig(builder: Builder, context: IZiweiContext, flowBig: Branch): List<Triple<Int, StemBranch, Int>> {
      val bigRangeImpl = context.bigRangeImpl
      val birthYear = builder.chineseDate.year
      val birthCycle = builder.chineseDate.cycleOrZero

      val (first, second) = bigRangeImpl.getVageRange(builder.branchHouseMap[flowBig]!!, builder.set, birthYear.stem, builder.gender, context.houseSeqImpl)

      // 再把虛歲轉換成干支
      return (first .. second).map { vAge ->
        val sb = birthYear.next(vAge - 1) // 虛歲 (vAge) 轉換為年 , 要減一 . 虛歲
        val cycle: Int
        cycle = if (sb.index >= birthYear.index) {
          birthCycle + (vAge - 1) / 60
        } else {
          birthCycle + (vAge - 1) / 60 + 1
        }
        Triple(cycle , sb , vAge)
      }.toList()
    }

    /**
     * @param flowYear 流年
     * @return 此流年有哪些流月（月份＋是否閏月）
     */
    fun getMonthsOf(cycle: Int, flowYear: StemBranch, chineseDateImpl: IChineseDate): List<Pair<Int, Boolean>> {

      return chineseDateImpl.getMonthsOf(cycle, flowYear)
    }

    /**
     * @param cycle     cycle
     * @param flowYear  流年
     * @param flowMonth 流月
     * @param leap      是否閏月
     * @return 該流月的日子 (陰曆＋陽曆＋干支）
     */
    fun getDaysOfMonth(chineseDateImpl: IChineseDate, cycle: Int, flowYear: StemBranch, flowMonth: Int, leap: Boolean): List<Triple<ChineseDate, ChronoLocalDate, StemBranch>> {
      val days = chineseDateImpl.getDaysOf(cycle, flowYear, flowMonth, leap)

      val list =  mutableListOf<Triple<ChineseDate, ChronoLocalDate, StemBranch>>()

      for (i in 1..days) {
        val yinDate = ChineseDate(cycle, flowYear, flowMonth, leap, i)

        val yangDate = chineseDateImpl.getYangDate(yinDate)
        val lmtJulDay = (TimeTools.getGmtJulDay(yangDate.atTime(LocalTime.MIDNIGHT)) + 0.5).toInt()
        val index = (lmtJulDay - 11) % 60
        val sb = StemBranch[index]
        list.add(Triple(yinDate, yangDate, sb))
      }
      return list
    }

    /**
     * 取得此日的12個時辰
     */
    fun getHoursOfDay(day: StemBranch): List<StemBranch> {
      return Branch.values().map { b ->
        val stem = StemBranchUtils.getHourStem(day.stem, b)
        StemBranch[stem, b]
      }.toList()
    }
  }
}
