/**
 * Created by smallufo on 2018-06-06.
 */
package destiny.core.chinese.eightwords

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.IntAgeNoteImpl
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarTransit
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import destiny.core.chinese.eightwords.FortuneLargeConfig.Impl.SolarTermsSpan
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import kotlin.math.abs

/**
 * 節氣星座過運法
 * 以太陽每過黃道15度 [destiny.core.calendar.SolarTerms] 放大 120倍
 * 每柱干支大運約為五年左右
 * 傳回的大運干支為 [destiny.core.chinese.StemBranchUnconstrained] , 可能會出現「甲丑」 這類干支
 *
 * 演算法與 [FortuneLargeSpanImpl] 類似
 */
@Named
class FortuneLargeSolarTermsSpanImpl(private val eightWordsFeature: EightWordsFeature,
                                     private val fortuneDirectionFeature: IFortuneDirectionFeature,
                                     @Named("intAge8wImpl")
                                     private val intAgeImpl: IIntAge,
                                     ageNoteImplMap: Map<IntAgeNoteImpl , IntAgeNote>,
                                     private val solarTermsImpl: ISolarTerms,
                                     private val starTransitImpl: IStarTransit) : AbstractFortuneLargeImpl(ageNoteImplMap) {

  private fun getAgeMap(toAge: Int, gmtJulDay: GmtJulDay, gender: Gender, location: ILocation): Map<Int, Pair<GmtJulDay, GmtJulDay>> {
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, toAge)
  }


  /** 順推大運 , 取得該命盤的幾條大運 */
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, count: Int, config: FortuneLargeConfig): List<FortuneData> {

    val eightWords = eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)
    val forward = fortuneDirectionFeature.getPersonModel(lmt, loc, gender, null, null)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)

    val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = getAgeMap(120, gmtJulDay, gender, loc)

    return getFortuneDataList(eightWords, forward, gmtJulDay, gender, ageMap, count, config.span, getAgeNoteImpls(config.intAgeNotes))
  }


  private fun getFortuneDataList(eightWords: IEightWords, forward: Boolean , gmtJulDay: GmtJulDay, gender: Gender, ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>, count: Int, fortuneMonthSpan: Double, ageNoteImpls: List<IntAgeNote>): List<FortuneData> {
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
  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, targetGmt: ChronoLocalDateTime<*>, config: FortuneLargeConfig): IStemBranch {
    val targetGmtJulDay = TimeTools.getGmtJulDay(targetGmt)
    require(targetGmtJulDay > gmtJulDay) { "targetGmt $targetGmt must be after birth's time : $gmtJulDay" }

    val eightWords: EightWords = eightWordsFeature.getModel(gmtJulDay, loc, config.eightWordsConfig)

    return getStemBranch(gmtJulDay, loc, eightWords, gender, targetGmtJulDay, config.span)
  }

  private fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, eightWords: IEightWords, gender: Gender, targetGmtJulDay: GmtJulDay, fortuneMonthSpan: Double): IStemBranch {
    // 大運是否順行
    val fortuneForward = fortuneDirectionFeature.getPersonModel(gmtJulDay, loc, gender, null, null)

    val diffSeconds = (targetGmtJulDay - gmtJulDay) * SECONDS_OF_DAY

    var resultStemBranch: IStemBranch = eightWords.month.let { StemBranchUnconstrained[it.stem, it.branch]!! }
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
  }


  override fun toString(locale: Locale): String {
    return SolarTermsSpan.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return SolarTermsSpan.asDescriptive().getDescription(locale)
  }

  companion object {
    private val logger = KotlinLogging.logger {}

    private val cache: Cache<Pair<GmtJulDay, Gender>, MutableMap<Int, GmtJulDay>> = Caffeine.newBuilder()
      .maximumSize(100)
      .expireAfterAccess(1, TimeUnit.MINUTES)
      .build()
  }


}
