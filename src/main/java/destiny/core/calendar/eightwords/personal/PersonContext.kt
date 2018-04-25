/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords.personal

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import destiny.astrology.Coordinate
import destiny.astrology.IStarTransit
import destiny.astrology.Planet
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.EightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.concurrent.getOrSet
import kotlin.math.abs

class PersonContext(

  private val eightWordsContext: EightWordsContext,

  /** 歲數實作  */
  private val intAgeImpl: IIntAge,

  /** 星體運行到某點的介面  */
  private val starTransitImpl: IStarTransit,

  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法  */
  private val fortuneDirectionImpl: IFortuneDirection,

  /** 歲數註解實作  */
  override val ageNoteImpls: List<IntAgeNote>,

  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年  */
  override val fortuneMonthSpan: Double = 120.0,

  /** 運：「日」的 span 倍數，內定 365，即：一日走一年  */
  override val fortuneDaySpan: Double = 365.0,

  /** 運 :「時辰」的 span 倍數，內定 365x12，即：一時辰走一年  */
  override val fortuneHourSpan: Double = (365 * 12).toDouble()
                   ) : IPersonContext, IEightWordsContext by eightWordsContext, IReverseFortuneSpan, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)


  private val cache: Cache<Pair<Double, Gender>, MutableMap<Int, Double>> = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(1, TimeUnit.MINUTES)
    .build<Pair<Double, Gender>, MutableMap<Int, Double>>()

  @Transient
  private val ewModelThreadLocal = ThreadLocal<IEightWordsContextModel>()

  @Transient
  private val ageMapThreadLocal = ThreadLocal<Map<Int, Pair<Double, Double>>>()

  override fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender): IPersonContextModel {

    val ewModel: IEightWordsContextModel = ewModelThreadLocal.getOrSet { eightWordsContext.getEightWordsContextModel(lmt, location, place) }

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    val ageMap: Map<Int, Pair<Double, Double>> = ageMapThreadLocal.getOrSet { getAgeMap(120, gmtJulDay, gender, location) }

    val t0 = System.currentTimeMillis()
    val fortuneDataList = getFortuneDataList(lmt , location , gender , 9)
    val t1 = System.currentTimeMillis()
    logger.info("get fortuneDataList , takes {} millis" , (t1-t0))
    fortuneDataList.forEach { println(it) }

    return PersonContextModel(ewModel, gender, fortuneDataList, ageMap)
  }

  /** 八字大運是否順行  */
  private fun isFortuneForward(gender: Gender, eightWords: EightWords): Boolean {
    return fortuneDirectionImpl.isForward(gender, eightWords)
  }

  /** 順推大運 , 取得該命盤的幾條大運 */
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {
    val ewModel: IEightWordsContextModel = ewModelThreadLocal.getOrSet { eightWordsContext.getEightWordsContextModel(lmt, location, null) }
    val eightWords = ewModel.eightWords
    val forward = isFortuneForward(gender , eightWords)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt , location)

    val ageMap: Map<Int, Pair<Double, Double>> = ageMapThreadLocal.getOrSet { getAgeMap(120, gmtJulDay, gender, location) }

    //下個大運的干支
    var nextStemBranch = if (forward) eightWords.month.next else eightWords.month.previous
    var i=1
    return generateSequence {
      // 距離下個「節」有幾秒
      val startFortuneSeconds = getTargetMajorSolarTermsSeconds(gmtJulDay, gender, i * if (forward) 1 else -1)
      val endFortuneSeconds = getTargetMajorSolarTermsSeconds(gmtJulDay, gender, (i + 1) * if (forward) 1 else -1)

      // 將距離秒數乘上倍數 (ex : 120) , 就可以得知 該大運的起點時刻
      val startFortuneGmtJulDay = gmtJulDay + abs(startFortuneSeconds) * fortuneMonthSpan / 86400.0
      val endFortuneGmtJulDay = gmtJulDay + abs(endFortuneSeconds) * fortuneMonthSpan / 86400.0

      /** 該大運起點 , 歲數為何 . 歲數的定義則由 [IIntAge] 處理 */
      val startFortuneAge = getAge(startFortuneGmtJulDay, ageMap) ?: 0
      val endFortuneAge = getAge(endFortuneGmtJulDay, ageMap) ?: 0

      /** 附加上 西元、民國 之類的註記 */
      val startFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> ageMap[startFortuneAge]?.let { impl.getAgeNote(it) } }.filter { it != null }
          .map { it!! }.toList()
      val endFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> ageMap[endFortuneAge]?.let { impl.getAgeNote(it) } }.filter { it != null }
          .map { it!! }.toList()

      val sb = StemBranch[nextStemBranch.stem , nextStemBranch.branch]
      nextStemBranch = if (forward) nextStemBranch.next else nextStemBranch.previous
      i++
      FortuneData(sb, startFortuneGmtJulDay, endFortuneGmtJulDay, startFortuneAge, endFortuneAge,
                    startFortuneAgeNotes, endFortuneAgeNotes)
    }.takeWhile { i <= count+1 }
      .toList()

  } // 順推大運 , 取得該命盤的幾條大運


  private fun getAgeMap(toAge: Int,
                        gmtJulDay: Double,
                        gender: Gender,
                        location: ILocation): Map<Int, Pair<Double, Double>> {
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, toAge)
  }

  /**
   * @return 在此 gmtJulDay 時刻，座落於歲數的哪一歲當中
   * 可能歲數超出範圍之後，或是根本在出生之前，就會傳回 empty
   */
  private fun getAge(gmtJulDay: Double, ageMap: Map<Int, Pair<Double, Double>>): Int? {
    return ageMap.entries.firstOrNull { (age, pair) -> gmtJulDay > pair.first && pair.second > gmtJulDay }?.key
  }


  /**
   * 距離下 index 個「節」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0
   *
   * @return 如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值
   */
  private fun getTargetMajorSolarTermsSeconds(gmtJulDay: Double, gender: Gender, index: Int): Double {
    require(index != 0) { "index cannot be 0 !" }

    val reverse = index < 0

    var stepGmtJulDay = gmtJulDay
    //現在的 節氣
    var currentSolarTerms = eightWordsContext.solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    var stepMajorSolarTerms = SolarTerms.getNextMajorSolarTerms(currentSolarTerms, reverse)

    var i: Int = if (!reverse) 1 else -1

    var hashMap: MutableMap<Int, Double>? = cache.getIfPresent(Pair(gmtJulDay, gender))

    if (hashMap == null) {
      hashMap = LinkedHashMap()
      cache.put(Pair(gmtJulDay, gender), hashMap)
    }

    var targetGmtJulDay: Double? = null
    if (hashMap.containsKey(index)) {
      targetGmtJulDay = hashMap[index]
      logger.debug("from map , index = {} , targetGmtJulDay exists , value = {}", index, targetGmtJulDay)
    }

    if (targetGmtJulDay == null) {
      if (!reverse) {
        //順推
        if (hashMap[index - 1] != null)
          i = index - 1

        while (i <= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          if (hashMap[i] == null) {
            logger.debug("順推 cache.get({}) miss", i)
            //沒有計算過
            targetGmtJulDay = starTransitImpl.getNextTransitGmt(Planet.SUN, stepMajorSolarTerms.zodiacDegree.toDouble(),
                                                                Coordinate.ECLIPTIC, stepGmtJulDay, true)
            //以隔天計算現在節氣
            stepGmtJulDay = targetGmtJulDay + 1

            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay, gender), hashMap)
          } else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap[i]
            stepGmtJulDay = targetGmtJulDay!! + 1
          }

          currentSolarTerms = eightWordsContext.solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay)
          stepMajorSolarTerms = SolarTerms.getNextMajorSolarTerms(currentSolarTerms, false)
          i++
        } // while (i <= index)
      } //順推
      else {
        //逆推
        if (hashMap[index + 1] != null)
          i = index + 1

        while (i >= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推

          if (hashMap[i] == null) {
            logger.debug("逆推 cache.get({}) miss", i)
            //沒有計算過

            targetGmtJulDay = starTransitImpl.getNextTransitGmt(Planet.SUN, stepMajorSolarTerms.zodiacDegree.toDouble(),
                                                                Coordinate.ECLIPTIC, stepGmtJulDay, false)
            //以前一天計算現在節氣
            stepGmtJulDay = targetGmtJulDay - 1
            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay, gender), hashMap)
          } else {
            //之前計算過
            logger.debug("逆推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap[i]
            stepGmtJulDay = targetGmtJulDay!! - 1 //LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = eightWordsContext.solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay)
          stepMajorSolarTerms = SolarTerms.getNextMajorSolarTerms(currentSolarTerms, true)
          i--
        } //while (i >= index)
      } //逆推
    }

    assert(targetGmtJulDay != null)

    // 同義於 Duration.between(gmt , targetGmtJulDay)
    val durDays = targetGmtJulDay!! - gmtJulDay
    logger.trace("durDays = {} ", durDays)
    return durDays * 86400

  } // getTargetMajorSolarTermsSeconds(int)

  /**
   * 由 GMT 反推大運
   *
   * @param lmt       出生時刻
   * @param location  出生地點
   * @param targetGmt 目標時刻（必須在出生時刻之後）
   * @param span      放大倍數
   * @return 干支
   */
  override fun getStemBranchOfFortune(lmt: ChronoLocalDateTime<*>,
                                      location: ILocation,
                                      gender: Gender,
                                      targetGmt: ChronoLocalDateTime<*>,
                                      span: Double): StemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    val gmt = TimeTools.getGmtFromLmt(lmt, location)


    require(targetGmt.isAfter(gmt)) { "targetGmt $targetGmt must be after birth's time : $gmt" }

    val ewModel: IEightWordsContextModel = eightWordsContext.getEightWordsContextModel(lmt, location, null)
    var resultStemBranch = ewModel.eightWords.month

    // 大運是否順行
    val fortuneForward = fortuneDirectionImpl.isForward(gender, ewModel.eightWords)

    val dur = Duration.between(targetGmt, gmt).abs()
    val diffSeconds = dur.seconds + dur.nano / 1_000_000_000.0

    if (fortuneForward) {
      logger.debug("大運順行")
      var index = 1
      while (getTargetMajorSolarTermsSeconds(gmtJulDay, gender, index) * span < diffSeconds) {
        resultStemBranch = resultStemBranch.next
        index++
      }
      return resultStemBranch
    } else {
      logger.debug("大運逆行")
      var index = -1
      while (abs(getTargetMajorSolarTermsSeconds(gmtJulDay, gender, index) * span) < diffSeconds) {
        resultStemBranch = resultStemBranch.previous
        index--
      }
      return resultStemBranch
    }
  }
}

