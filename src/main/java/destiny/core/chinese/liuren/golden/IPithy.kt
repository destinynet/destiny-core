/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.*
import destiny.core.chinese.liuren.General
import destiny.core.chinese.liuren.IGeneralSeq
import destiny.core.chinese.liuren.IGeneralStemBranch
import org.slf4j.LoggerFactory
import java.time.chrono.ChronoLocalDateTime

interface IPithy {

  fun getPithy(direction: Branch, ew: EightWords, 月將: Branch, tianyiImpl: ITianyi, dayNight: DayNight,
               clockwise: Clockwise, seq: IGeneralSeq, generalStemBranchImpl: IGeneralStemBranch): Pithy {

    // 天乙貴人(起點)
    val 天乙貴人 = tianyiImpl.getFirstTianyi(ew.day.stem, dayNight)

    val steps = when (clockwise) {
      Clockwise.CLOCKWISE -> direction.getAheadOf(天乙貴人)
      Clockwise.COUNTER -> 天乙貴人.getAheadOf(direction)
    }

    logger.debug("天乙貴人 (日干 {} + {} ) = {} . 地分 = {} , 順逆 = {} , steps = {}",
                 ew.day.stem, dayNight, 天乙貴人, direction, clockwise, steps)

    // 貴神
    val 貴神地支 = General.貴人.next(steps, seq).getStemBranch(generalStemBranchImpl).branch
    val 貴神天干 = StemBranchUtils.getHourStem(ew.day.stem, 貴神地支)
    logger.debug("推導貴神，從 {} 開始走 {} 步，得到 {} , 地支為 {} , 天干為 {}", General.貴人, steps, General.貴人.next(steps, seq), 貴神地支,
                 貴神天干)
    val 貴神 = StemBranch.get(貴神天干, 貴神地支)

    return Pithy(ew, direction, 月將, dayNight, 貴神)
  }

  fun getPithy(direction: Branch, lmt: ChronoLocalDateTime<*>, loc: ILocation, monthBranchImpl: IMonthMaster,
               dayNightImpl: DayNightDifferentiator, tianyiImpl: ITianyi, clockwiseImpl: IClockwise,
               seq: IGeneralSeq, generalStemBranchImpl: IGeneralStemBranch, eightWordsImpl: IEightWords): Pithy {
    val ew = eightWordsImpl.getEightWords(lmt, loc)

    val 月將 = monthBranchImpl.getBranch(lmt, loc)
    logger.debug("月將 = {}", 月將)

    val clockwise = clockwiseImpl.getClockwise(lmt, loc)

    val dayNight = dayNightImpl.getDayNight(lmt, loc)

    return getPithy(direction, ew, 月將, tianyiImpl, dayNight, clockwise, seq, generalStemBranchImpl)
  }

  companion object {

    val logger = LoggerFactory.getLogger(IPithy::class.java)
  }

}
