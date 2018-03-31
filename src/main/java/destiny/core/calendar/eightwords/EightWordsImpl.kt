/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.子
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

class EightWordsImpl(val yearMonthImpl: IYearMonth          // 換年, 以及月支計算的實作
                     , val dayImpl: IDay                // 計算日干支的介面
                     , val hourImpl: IHour               // 計算時支的介面
                     , val midnightImpl: IMidnight            // 計算「子正」的介面
                     , changeDayAfterZi: Boolean) : IEightWordsFactory, Serializable {
  var isChangeDayAfterZi = true
    internal set // 子初是否換日，內定是：true (換日)

  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    this.isChangeDayAfterZi = changeDayAfterZi
  }


  override fun getEightWords(gmtJulDay: Double, loc: ILocation): EightWords {
    val year = yearMonthImpl.getYear(gmtJulDay, loc)

    val month = yearMonthImpl.getMonth(gmtJulDay, loc)
    val day = dayImpl.getDay(gmtJulDay, loc, midnightImpl, hourImpl, isChangeDayAfterZi)
    var 臨時日干 = day.stem
    val 時支 = this.hourImpl.getHour(gmtJulDay, loc)

    val 時干: Stem

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

    val nextZi = hourImpl.getLmtNextStartOf(lmt, loc, 子, revJulDayFunc)

    // 如果「子正」才換日
    if (!isChangeDayAfterZi) {
      /**
       * <pre>
       * 而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
       * 第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
       * 第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
       *
       * 子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
       * |------------------|--------------------|--------------------|------------------|
      </pre> *
       */
      if (day !== dayImpl.getDay(nextZi, loc, midnightImpl, hourImpl, isChangeDayAfterZi))
        臨時日干 = Stem[臨時日干.index + 1]
    }

    時干 = when (Stem.getIndex(臨時日干)) {
      0, 5 -> Stem[Branch.getIndex(時支)]
      1, 6 -> Stem[Branch.getIndex(時支) + 2]
      2, 7 -> Stem[Branch.getIndex(時支) + 4]
      3, 8 -> Stem[Branch.getIndex(時支) + 6]
      4, 9 -> Stem[Branch.getIndex(時支) + 8]
      else -> throw AssertionError("Error")
    }
    return EightWords(year, month, day, StemBranch.get(時干, 時支))
  }

  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   */
  override fun getEightWords(lmt: ChronoLocalDateTime<*>, loc: ILocation): EightWords {

    val year = yearMonthImpl.getYear(lmt, loc)
    val month = yearMonthImpl.getMonth(lmt, loc)
    val day = dayImpl.getDay(lmt, loc, midnightImpl, hourImpl, isChangeDayAfterZi)
    var 臨時日干 = day.stem
    val 時支 = this.hourImpl.getHour(lmt, loc)

    val 時干: Stem

    val nextZi = hourImpl.getLmtNextStartOf(lmt, loc, 子, revJulDayFunc)

    // 如果「子正」才換日
    if (!isChangeDayAfterZi) {
      /**
       * <pre>
       * 而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
       * 第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
       * 第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
       *
       * 子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
       * |------------------|--------------------|--------------------|------------------|
      </pre> *
       */
      if (day !== dayImpl.getDay(nextZi, loc, midnightImpl, hourImpl, isChangeDayAfterZi))
        臨時日干 = Stem[臨時日干.index + 1]
    }

    時干 = when (Stem.getIndex(臨時日干)) {
      0, 5 -> Stem[Branch.getIndex(時支)]
      1, 6 -> Stem[Branch.getIndex(時支) + 2]
      2, 7 -> Stem[Branch.getIndex(時支) + 4]
      3, 8 -> Stem[Branch.getIndex(時支) + 6]
      4, 9 -> Stem[Branch.getIndex(時支) + 8]
      else -> throw AssertionError("Error")
    }
    return EightWords(year, month, day, StemBranch[時干, 時支])
  }

  companion object {

    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
