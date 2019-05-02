/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.子
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.concurrent.TimeUnit

class EightWordsImpl(val yearMonthImpl: IYearMonth      // 換年, 以及月支計算的實作
                     , val dayHourImpl : IDayHour       // 日、時 的實作
) : IEightWordsFactory, Serializable {

  private data class CacheKeyGmt(val gmtJulDay: Double, val loc: ILocation)

  private val cacheGmt: Cache<CacheKeyGmt, IEightWords> = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10, TimeUnit.SECONDS)
    .build()


  override fun getEightWords(gmtJulDay: Double, loc: ILocation): IEightWords {

    fun inner(): IEightWords {
      val year = yearMonthImpl.getYear(gmtJulDay, loc)

      val month = yearMonthImpl.getMonth(gmtJulDay, loc)
      val day = dayHourImpl.getDay(gmtJulDay, loc)
      var 臨時日干 = day.stem
      val 時支 = this.dayHourImpl.getHour(gmtJulDay, loc)

      val 時干: Stem

      val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

      val nextZi = dayHourImpl.getLmtNextStartOf(lmt, loc, 子, revJulDayFunc)

      // 如果「子正」才換日
      if (!dayHourImpl.changeDayAfterZi) {
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
        if (day !== dayHourImpl.getDay(nextZi, loc))
          臨時日干 = Stem[臨時日干.index + 1]
      }


      時干 = getHourStem(臨時日干 , 時支)

//      時干 = when (Stem.getIndex(臨時日干)) {
//        0, 5 -> Stem[Branch.getIndex(時支)]
//        1, 6 -> Stem[Branch.getIndex(時支) + 2]
//        2, 7 -> Stem[Branch.getIndex(時支) + 4]
//        3, 8 -> Stem[Branch.getIndex(時支) + 6]
//        4, 9 -> Stem[Branch.getIndex(時支) + 8]
//        else -> throw AssertionError("Error")
//      }
      return EightWords(year, month, day, StemBranch[時干, 時支])
    }

    val key = CacheKeyGmt(gmtJulDay, loc)

    return cacheGmt.get(key) { inner() }

  }


  private data class CacheKeyLmt(val lmt: ChronoLocalDateTime<*>, val loc: ILocation)

  private val cacheLmt: Cache<CacheKeyLmt, IEightWords> = CacheBuilder.newBuilder()
    .maximumSize(100)
    //.expireAfterWrite(5, TimeUnit.SECONDS)
    .expireAfterAccess(10, TimeUnit.SECONDS)
    .build()

  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   */
  override fun getEightWords(lmt: ChronoLocalDateTime<*>, loc: ILocation): IEightWords {

    fun inner(): IEightWords {
      val year = yearMonthImpl.getYear(lmt, loc)
      val month = yearMonthImpl.getMonth(lmt, loc)
      val day = dayHourImpl.getDay(lmt, loc)
      var 臨時日干 = day.stem
      val 時支 = this.dayHourImpl.getHour(lmt, loc)

      val 時干: Stem

      val nextZi = dayHourImpl.getLmtNextStartOf(lmt, loc, 子, revJulDayFunc)

      // 如果「子正」才換日
      if (!dayHourImpl.changeDayAfterZi) {
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
        if (day !== dayHourImpl.getDay(nextZi, loc))
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

    val key = CacheKeyLmt(lmt, loc)

    return cacheLmt.get(key) { inner() }
  }



  companion object {

    /** 五鼠遁日 */
    fun getHourStem(dayStem : Stem , hourBranch : Branch) : Stem {
      return when (Stem.getIndex(dayStem)) {
        0, 5 -> Stem[Branch.getIndex(hourBranch)]
        1, 6 -> Stem[Branch.getIndex(hourBranch) + 2]
        2, 7 -> Stem[Branch.getIndex(hourBranch) + 4]
        3, 8 -> Stem[Branch.getIndex(hourBranch) + 6]
        4, 9 -> Stem[Branch.getIndex(hourBranch) + 8]
        else -> throw AssertionError("Error")
      }
    }

    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
