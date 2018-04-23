/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords.personal

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
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.concurrent.TimeUnit

/** 用以取代 [PersonContext] */
class PersonContext2(

  private val eightWordsContext: EightWordsContext,

  /** 歲數實作  */
  private val intAgeImpl: IIntAge,

  /** 星體運行到某點的介面  */
  private val starTransitImpl: IStarTransit,

  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法  */
  private val fortuneDirectionImpl: IFortuneDirection,

  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年  */
  private val fortuneMonthSpan: Double=120.0,

  /** 歲數註解實作  */
  val ageNoteImpls: List<IntAgeNote>
                    ) : IPersonContext , IEightWordsContext by eightWordsContext , Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)


  private val cache = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .build<Pair<Double , Gender>, MutableMap<Int, Double>>()

  override fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender): IPersonContextModel {

    val ewModel: IEightWordsContextModel = eightWordsContext.getEightWordsContextModel(lmt, location, place)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt , location)

    val ageMap = getAgeMap(120 , gmtJulDay , gender , location)

    //val gmt: ChronoLocalDateTime<*> = TimeTools.getGmtFromLmt(lmt, location)

    // forward : 大運是否順行
    val forward = isFortuneDirectionForward(gender , ewModel.eightWords)
    val fortuneDataList = getFortuneDatas(9, forward, ewModel.eightWords, gmtJulDay, gender , ageMap)

    return PersonContextModel(ewModel , gender , fortuneDataList , ageMap)
  }

  /** 八字大運是否順行  */
  private fun isFortuneDirectionForward(gender: Gender, eightWords: EightWords) : Boolean {
    return fortuneDirectionImpl.isForward(gender , eightWords)
  }

  /**
   * @param count 計算 n柱 大運的資料
   */
  private fun getFortuneDatas(count: Int,
                              forward: Boolean,
                              eightWords: EightWords,
                              gmtJulDay: Double,
                              gender: Gender,
                              ageMap: Map<Int, Pair<Double, Double>>): List<FortuneData> {
        //下個大運的干支
    var nextStemBranch = if (forward) eightWords.month.next else eightWords.month.previous

    val fortuneDatas = mutableListOf<FortuneData>()


    // 計算九柱大運的相關資訊
    for (i in 1..count) {
      // 西元/民國/實歲/虛歲之值
      val startFortuneSeconds = getTargetMajorSolarTermsSeconds(gmtJulDay , gender ,  i * if (forward) 1 else -1)
      val   endFortuneSeconds = getTargetMajorSolarTermsSeconds(gmtJulDay , gender , (i + 1) * if (forward) 1 else -1)

      val startFortuneGmtJulDay = gmtJulDay + Math.abs(startFortuneSeconds) * fortuneMonthSpan / 86400.0
      val   endFortuneGmtJulDay = gmtJulDay + Math.abs(  endFortuneSeconds) * fortuneMonthSpan / 86400.0

      val startFortune = getAge(startFortuneGmtJulDay, ageMap) ?: 0
      val   endFortune = getAge(  endFortuneGmtJulDay, ageMap) ?: 0

      val startFortuneAgeNotes = ageNoteImpls.map { impl -> ageMap[startFortune]?.let { impl.getAgeNote(it) } }.filter { it != null }.map { it!! }.toList()
      val endFortuneAgeNotes   = ageNoteImpls.map { impl -> ageMap[  endFortune]?.let { impl.getAgeNote(it) } }.filter { it != null }.map { it!! }.toList()

      val fortuneData = FortuneData(nextStemBranch, startFortuneGmtJulDay, endFortuneGmtJulDay, startFortune, endFortune, startFortuneAgeNotes, endFortuneAgeNotes)
      fortuneDatas.add(fortuneData)

      nextStemBranch = if (forward) nextStemBranch.next else nextStemBranch.previous
    } // for 1 ~ fortunes)
    return fortuneDatas
  }

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
   * 距離下 N 個「節」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0
   *
   * @return **如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值**
   */
  private fun getTargetMajorSolarTermsSeconds(gmtJulDay : Double , gender: Gender , index: Int): Double {
    if (index == 0)
      throw RuntimeException("index cannot be 0 !")

    val reverse = index < 0

    var stepGmtJulDay = gmtJulDay
    //現在的 節氣
    var currentSolarTerms = eightWordsContext.solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    var stepMajorSolarTerms = SolarTerms.getNextMajorSolarTerms(currentSolarTerms, reverse)

    var i: Int = if (!reverse) 1 else -1

    var hashMap: MutableMap<Int, Double>? = cache.getIfPresent(Pair(gmtJulDay , gender))


    if (hashMap == null) {
      hashMap = LinkedHashMap()
      cache.put(Pair(gmtJulDay , gender), hashMap)
    }

    var targetGmtJulDay: Double? = null
    if (hashMap.containsKey(index))
      targetGmtJulDay = hashMap[index]

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
            stepGmtJulDay = targetGmtJulDay + 1  //LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);


            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay , gender), hashMap)
          } else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap[i]
            stepGmtJulDay = targetGmtJulDay!! + 1// LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);
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
            stepGmtJulDay = targetGmtJulDay - 1 // LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
            hashMap[i] = targetGmtJulDay
            cache.put(Pair(gmtJulDay , gender), hashMap)
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

}