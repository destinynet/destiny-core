/**
 * Created by smallufo on 2019-02-28.
 */
package destiny.core.chinese

import destiny.core.astrology.IZodiacSign
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.ZodiacSign.*
import destiny.core.calendar.ILocation
import jakarta.inject.Named
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IYearSplitterBySign {
  fun getYinYang(sign: ZodiacSign): IYinYang
}

interface IYearSplitter : IYearSplitterBySign {
  fun getYinYang(lmt: ChronoLocalDateTime<*>, location: ILocation): IYinYang
}


/**
 * 冬至 [ZodiacSign.CAPRICORN] 至 夏至 [ZodiacSign.GEMINI] 為 前半年 (陽) : 丑 -> 子 ... -> 酉 -> 申
 * 夏至 [ZodiacSign.GEMINI] 至 冬至 [ZodiacSign.CAPRICORN] 為 後半年 (陰) : 未 -> 午 ... -> 卯 -> 寅
 */
@Named
class YearSplitterSignImpl(private val zodiacSignImpl: IZodiacSign) : IYearSplitter, Serializable {
  
  override fun getYinYang(sign: ZodiacSign): YinYang {
    return if (formerHalfYear.contains(sign)) {
      YinYang.陽
    } else {
      YinYang.陰
    }
  }

  override fun getYinYang(lmt: ChronoLocalDateTime<*>, location: ILocation): YinYang {
    val sign = zodiacSignImpl.getSignsBetween(Planet.SUN, lmt, location).current.first
    return getYinYang(sign)
  }

  companion object {

    // 冬至 到 夏至 (前半年)
    val formerHalfYear = setOf(CAPRICORN, AQUARIUS, PISCES, ARIES, TAURUS, GEMINI)

    // 夏至 到 冬至 (後半年)
    val laterHalfYear = values().toSet().minus(formerHalfYear)
  }
}
