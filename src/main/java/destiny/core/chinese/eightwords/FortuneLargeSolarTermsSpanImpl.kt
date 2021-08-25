/**
 * Created by smallufo on 2018-06-06.
 */
package destiny.core.chinese.eightwords

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarTransit
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import mu.KotlinLogging
import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * 節氣星座過運法
 * 以太陽每過黃道15度 [destiny.core.calendar.SolarTerms] 放大 120倍
 * 每柱干支大運約為五年左右
 * 傳回的大運干支為 [destiny.core.chinese.StemBranchUnconstrained] , 可能會出現「甲丑」 這類干支
 *
 * 演算法與 [FortuneLargeSpanImpl] 類似
 */
class FortuneLargeSolarTermsSpanImpl(
  override val eightWordsImpl: IEightWordsStandardFactory,
  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法  */
  private val fortuneDirectionImpl: IFortuneDirection,
  /** 歲數實作  */
  private val intAgeImpl: IIntAge,
  private val solarTermsImpl: ISolarTerms,
  private val starTransitImpl: IStarTransit,
  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年  */
  override val fortuneMonthSpan: Double = 120.0,
  /** 歲數註解實作  */
  override val ageNoteImpls: List<IntAgeNote>) : IPersonFortuneLarge, IFortuneMonthSpan, Serializable {

  private fun getAgeMap(toAge: Int,
                        gmtJulDay: GmtJulDay,
                        gender: Gender,
                        location: ILocation): Map<Int, Pair<GmtJulDay, GmtJulDay>> {
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, toAge)
  }


  /** 順推大運 , 取得該命盤的幾條大運 */
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  loc: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {
    val eightWords: IEightWords = eightWordsImpl.getEightWords(lmt, loc)
    val forward = fortuneDirectionImpl.isForward(lmt, loc, gender)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)

    val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = getAgeMap(120, gmtJulDay, gender, loc)

    //下個大運的干支
    var i = 1

    return generateSequence {
      // 距離下個「節」或「氣」有幾秒
      val startFortuneSeconds = getTargetSolarTermsSeconds(gmtJulDay, gender, i * if (forward) 1 else -1)
      val endFortuneSeconds = getTargetSolarTermsSeconds(gmtJulDay, gender, (i + 1) * if (forward) 1 else -1)

      // 將距離秒數乘上倍數 (ex : 120) , 就可以得知 該大運的起點時刻
      val startFortuneGmtJulDay = gmtJulDay + abs(startFortuneSeconds) * fortuneMonthSpan / SECONDS_OF_DAY.toDouble()
      val endFortuneGmtJulDay = gmtJulDay + abs(endFortuneSeconds) * fortuneMonthSpan / SECONDS_OF_DAY.toDouble()

      /** 該大運起點 , 歲數為何 . 歲數的定義則由 [IIntAge] 處理 */
      val startFortuneAge: Int = getAge(startFortuneGmtJulDay, ageMap) ?: 0
      val endFortuneAge: Int = getAge(endFortuneGmtJulDay, ageMap) ?: 0

      /** 附加上 西元、民國 之類的註記 */
      val startFortuneAgeNotes: List<String> =
        ageNoteImpls.mapNotNull { impl -> ageMap[startFortuneAge]?.let { impl.getAgeNote(it) } }.toList()
      val endFortuneAgeNotes: List<String> =
        ageNoteImpls.mapNotNull { impl -> ageMap[endFortuneAge]?.let { impl.getAgeNote(it) } }.toList()

      val sbu = eightWords.month.let { StemBranchUnconstrained[it.stem, it.branch]!! }
        .let { if (forward) it.next(i) else it.prev(i) }
      //val sb: StemBranch = eightWords.month.let { if (forward) it.next(i) else it.prev(i) }
      i++
      FortuneData(
        sbu, startFortuneGmtJulDay, endFortuneGmtJulDay, startFortuneAge, endFortuneAge,
        startFortuneAgeNotes, endFortuneAgeNotes
      )
    }.takeWhile { i <= count + 1 }
      .toList()
  }


  /**
   * 距離下 index 個「節」或「氣」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0
   *
   * @return 如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值
   */
  private fun getTargetSolarTermsSeconds(gmtJulDay: GmtJulDay, gender: Gender, index: Int): Double {
    require(index != 0) { "index cannot be 0 !" }

    val reverse = index < 0
    var stepGmtJulDay = gmtJulDay
    //現在的 節氣
    val currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    //var stepSolarTerms = if (reverse) currentSolarTerms.previous() else currentSolarTerms.next()
    var stepSolarTerms = if (reverse) currentSolarTerms else currentSolarTerms.next()

    var i: Int = if (!reverse) 1 else -1

    var hashMap: MutableMap<Int, GmtJulDay>? = cache.getIfPresent(Pair(gmtJulDay, gender))

    if (hashMap == null) {
      hashMap = LinkedHashMap()
      cache.put(Pair(gmtJulDay, gender), hashMap)
    }

    var targetGmtJulDay: GmtJulDay? = null
    if (hashMap.containsKey(index)) {
      targetGmtJulDay = hashMap[index]
      logger.debug("from map , index = {} , targetGmtJulDay exists , value = {}", index, targetGmtJulDay)
    }

    if (targetGmtJulDay == null) {
      if (!reverse) {
        // 順推
        if (hashMap[index - 1] != null)
          i = index - 1

        while (i <= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          if (hashMap[i] == null) {
            logger.debug("順推 cache.get({}) miss", i)

            //沒有計算過
            targetGmtJulDay = starTransitImpl.getNextTransitGmt(
              Planet.SUN, stepSolarTerms.zodiacDegree.toZodiacDegree(), stepGmtJulDay, true, Coordinate.ECLIPTIC
            )

            logger.debug("[順] 計算 {} 日期 = {}", stepSolarTerms, targetGmtJulDay)
            //以隔天計算現在節氣
            stepGmtJulDay = targetGmtJulDay + 1

            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay, gender), hashMap)
          } else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap.getValue(i)
            stepGmtJulDay = targetGmtJulDay + 1
          }

          stepSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay).next()
          i++
        } // while (i <= index)
      } // 順推
      else {
        // 逆推
        if (hashMap[index + 1] != null)
          i = index + 1

        while (i >= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推

          if (hashMap[i] == null) {
            logger.debug("逆推 cache.get({}) miss", i)
            //沒有計算過

            targetGmtJulDay = starTransitImpl.getNextTransitGmt(
              Planet.SUN, stepSolarTerms.zodiacDegree.toZodiacDegree(), stepGmtJulDay, false, Coordinate.ECLIPTIC
            )
            logger.debug("[逆] 計算 {} 日期 = {}", stepSolarTerms, targetGmtJulDay)
            //以前一天計算現在節氣
            stepGmtJulDay = targetGmtJulDay - 1
            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay, gender), hashMap)
          } else {
            //之前計算過
            logger.debug("逆推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap.getValue(i)
            stepGmtJulDay = targetGmtJulDay - 1
          }

          stepSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay)
          i--
        } //while (i >= index)
      } // 逆推
    }

    assert(targetGmtJulDay != null)

    // 同義於 Duration.between(gmt , targetGmtJulDay)
    val durDays = targetGmtJulDay!! - gmtJulDay
    logger.debug("targetGmtJulDay {} - gmtJulDay {} : durDays = {} ", targetGmtJulDay, gmtJulDay, durDays)
    return durDays * SECONDS_OF_DAY
  } // getTargetSolarTermsSeconds

  /**
   * @return 在此 gmtJulDay 時刻，座落於歲數的哪一歲當中
   * 可能歲數超出範圍之後，或是根本在出生之前，就會傳回 empty
   */
  private fun getAge(gmtJulDay: GmtJulDay, ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>): Int? {
    return ageMap.entries.firstOrNull { (_, pair) -> gmtJulDay > pair.first && pair.second > gmtJulDay }?.key
  }


  /**
   * 逆推大運 , 求，未來某時刻，的大運干支為何
   */
  override fun getStemBranch(lmt: ChronoLocalDateTime<*>,
                             location: ILocation,
                             gender: Gender,
                             targetGmt: ChronoLocalDateTime<*>): IStemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    val gmt = TimeTools.getGmtFromLmt(lmt, location)

    require(targetGmt.isAfter(gmt)) { "targetGmt $targetGmt must be after birth's time : $gmt" }

    val eightWords: IEightWords = eightWordsImpl.getEightWords(lmt, location)
    var resultStemBranch: IStemBranch = eightWords.month.let { StemBranchUnconstrained[it.stem, it.branch]!! }

    // 大運是否順行
    val fortuneForward = fortuneDirectionImpl.isForward(lmt, location, gender)

    val dur = Duration.between(targetGmt, gmt).abs()
    val diffSeconds = dur.seconds + dur.nano / 1_000_000_000.0

    return if (fortuneForward) {
      logger.debug("大運順行")
      var index = 1

      while (getTargetSolarTermsSeconds(gmtJulDay, gender, index) * fortuneMonthSpan < diffSeconds) {
        resultStemBranch = resultStemBranch.next
        index++
      }
      resultStemBranch
    } else {
      logger.debug("大運逆行")
      var index = -1
      while (abs(getTargetSolarTermsSeconds(gmtJulDay, gender, index) * fortuneMonthSpan) < diffSeconds) {
        resultStemBranch = resultStemBranch.prev
        index--
      }
      resultStemBranch
    }
  } // 逆推大運

  override fun toString(locale: Locale): String {
    return "「節」＋「氣（星座）」過運"
  }

  override fun getDescription(locale: Locale): String {
    return "除了傳統法，額外考量「星座」（意即：中氣）過運。通常一柱大運為五年。"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FortuneLargeSolarTermsSpanImpl) return false

    if (eightWordsImpl != other.eightWordsImpl) return false
    if (fortuneDirectionImpl != other.fortuneDirectionImpl) return false
    if (intAgeImpl != other.intAgeImpl) return false
    if (solarTermsImpl != other.solarTermsImpl) return false
    if (starTransitImpl != other.starTransitImpl) return false
    if (fortuneMonthSpan != other.fortuneMonthSpan) return false
    if (ageNoteImpls != other.ageNoteImpls) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eightWordsImpl.hashCode()
    result = 31 * result + fortuneDirectionImpl.hashCode()
    result = 31 * result + intAgeImpl.hashCode()
    result = 31 * result + solarTermsImpl.hashCode()
    result = 31 * result + starTransitImpl.hashCode()
    result = 31 * result + fortuneMonthSpan.hashCode()
    result = 31 * result + ageNoteImpls.hashCode()
    return result
  }


  companion object {
    private val logger = KotlinLogging.logger {}

    private val cache: Cache<Pair<GmtJulDay, Gender>, MutableMap<Int, GmtJulDay>> = Caffeine.newBuilder()
      .maximumSize(100)
      .expireAfterAccess(1, TimeUnit.MINUTES)
      .build()
  }


}
