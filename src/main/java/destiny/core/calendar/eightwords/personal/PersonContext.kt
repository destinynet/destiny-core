/**
 * Created by smallufo on 2015-04-21.
 */
package destiny.core.calendar.eightwords.personal

import com.google.common.cache.CacheBuilder
import destiny.astrology.*
import destiny.astrology.Coordinate.ECLIPTIC
import destiny.astrology.Planet.SUN
import destiny.core.Gender
import destiny.core.IntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.Location
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class PersonContext(
  eightWordsImpl: IEightWords,
  chineseDateImpl: IChineseDate,
  yearMonthImpl: IYearMonth,
  dayImpl: IDay,
  hourImpl: IHour,
  midnightImpl: IMidnight,
  changeDayAfterZi: Boolean,

  /** 實作計算節氣的介面  */
  private val solarTermsImpl: ISolarTerms,

  /** 星體運行到某點的介面  */
  private val starTransitImpl: IStarTransit,

  /** 歲數實作  */
  private val intAgeImpl: IntAge,

  /** 出生時刻  */
  lmt: ChronoLocalDateTime<*>,

  /** 出生地點  */
  location: Location,

  private val place: String?,
  /** 性別  */
  val gender: Gender,

  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年  */
  private val fortuneMonthSpan: Double,
  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法  */
  private val fortuneDirectionImpl: IFortuneDirection?,
  risingSignImpl: IRisingSign,
  starPositionImpl: IStarPosition<*>,
  /** 歲數註解實作  */
  val ageNoteImpls: List<IntAgeNote>) : EightWordsContext(lmt, location, eightWordsImpl, yearMonthImpl, chineseDateImpl, dayImpl, hourImpl, midnightImpl, changeDayAfterZi, risingSignImpl, starPositionImpl) {

  private val logger = LoggerFactory.getLogger(javaClass)


  /** 現在（LMT）的節/氣  */
//  private override val currentSolarTerms: SolarTerms?


  /** 運：「日」的 span 倍數，內定 365，即：一日走一年  */
  private val fortuneDaySpan = 365.0

  /** 運 :「時辰」的 span 倍數，內定 365x12，即：一時辰走一年  */
  /** 運 : 時干支的倍數  */
  private val fortuneHourSpan = (365 * 12).toDouble()

  /** 大運輸出格式  */
  //private final FortuneOutput fortuneOutput;

  private val cache = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .build<PersonContext, MutableMap<Int, Double>>()


  override val model: PersonContextModel
    get() = PersonContextModel(gender, eightWords, lmt, location, place,
                               chineseDate,
                               getFortuneDatas(9),
                               risingStemBranch,
                               getBranchOf(Planet.SUN),
                               getBranchOf(Planet.MOON),
                               prevNextMajorSolarTerms,
                               getAgeMap(90))

  /** 八字大運是否順行  */
  val isFortuneDirectionForward: Boolean
    get() = this.fortuneDirectionImpl!!.isForward(this)


  /**
   * 計算此時刻，距離上一個「節」有幾秒，距離下一個「節」又有幾秒
   */
  // 現在（亦即：上一個節）的「節」
  // 如果是「中氣」的話
  // 再往前取一個 , 即可得到「節」
  // 下一個「節」
  val majorSolarTermsBetween: Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>>
    get() {
      val gmt = TimeTools.getGmtFromLmt(lmt, location)
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
      var prevMajorSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt)
      if (!prevMajorSolarTerms.isMajor)
        prevMajorSolarTerms = prevMajorSolarTerms.previous()

      val prevGmt = starTransitImpl.getNextTransitGmtDateTime(SUN, prevMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, false)
      val dur1 = Duration.between(gmt, prevGmt).abs()
      val d1 = dur1.seconds + dur1.nano / 1_000_000_000.0
      val nextMajorSolarTerms = this.getNextMajorSolarTerms(prevMajorSolarTerms, false)
      val nextGmt = starTransitImpl.getNextTransitGmtDateTime(SUN, nextMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, true)
      val dur2 = Duration.between(gmt, nextGmt).abs()
      val d2 = dur2.seconds + dur2.nano / 1_000_000_000.0

      logger.debug(" prevGmt = {}", prevGmt)
      logger.debug("(now)Gmt = {}", gmt)
      logger.debug(" nextGmt = {}", nextGmt)
      return Pair(Pair(prevMajorSolarTerms, d1), Pair(nextMajorSolarTerms, d2))
    }

  private fun getAgeMap(toAge: Int): Map<Int, Pair<Double, Double>> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, toAge)
  }

  /**
   * 距離下 N 個「節」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0
   *
   * @return **如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值**
   */
  fun getTargetMajorSolarTermsSeconds(index: Int): Double {
    if (index == 0)
      throw RuntimeException("index cannot be 0 !")

    var reverse = false
    if (index < 0)
      reverse = true

    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    //double stepGmt = TimeTools.getGmtJulDay(gmt);
    var stepGmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    //現在的 節氣
    var currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt)
    var stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, reverse)

    var i: Int
    i = if (!reverse)
      1
    else
      -1

    var hashMap: MutableMap<Int, Double>? = cache.getIfPresent(this)


    if (hashMap == null) {
      hashMap = LinkedHashMap()
      cache.put(this, hashMap)
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
            targetGmtJulDay = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, stepGmtJulDay, true)
            //以隔天計算現在節氣
            stepGmtJulDay = targetGmtJulDay + 1  //LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);


            hashMap[i] = targetGmtJulDay
            cache.put(this, hashMap)
          } else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit", i)
            targetGmtJulDay = hashMap[i]
            stepGmtJulDay = targetGmtJulDay!! + 1// LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay)
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, false)
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
            //沒有計算過

            targetGmtJulDay = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, stepGmtJulDay, false)
            //以前一天計算現在節氣
            stepGmtJulDay = targetGmtJulDay - 1 // LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
            hashMap[i] = targetGmtJulDay
            cache.put(this, hashMap)
          } else {
            //之前計算過
            targetGmtJulDay = hashMap[i]
            stepGmtJulDay = targetGmtJulDay!! - 1 //LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay)
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, true)
          i--
        } //while (i >= index)
      } //逆推
    }

    assert(targetGmtJulDay != null)

    // 同義於 Duration.between(gmt , targetGmtJulDay)
    val durDays = targetGmtJulDay!! - TimeTools.getGmtJulDay(gmt)
    logger.trace("durDays = {} ", durDays)
    return durDays * 86400

  } // getTargetMajorSolarTermsSeconds(int)


  /**
   * 取得下一個「節」
   *
   * @param currentSolarTerms 現在的「節」
   * @param reverse           是否逆推
   * @return 下一個「節」（如果 reverse == true，則傳回上一個「節」）
   */
  private fun getNextMajorSolarTerms(currentSolarTerms: SolarTerms, reverse: Boolean): SolarTerms {
    val currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms)
    return if (currentSolarTermsIndex % 2 == 0) {
      //立春 , 驚蟄 , 清明 ...
      if (!reverse)
      //順推
        currentSolarTerms.next().next()
      else
        currentSolarTerms
    } else {
      //雨水 , 春分 , 穀雨 ...
      if (!reverse)
      //順推
        currentSolarTerms.next()
      else
        currentSolarTerms.previous()
    }
  } // getNextMajorSolarTerms()

  /**
   * @return 在此 gmtJulDay 時刻，座落於歲數的哪一歲當中
   * 可能歲數超出範圍之後，或是根本在出生之前，就會傳回 empty
   */
  private fun getAge(gmtJulDay: Double, ageMap: Map<Int, Pair<Double, Double>>): Int? {
    return ageMap.entries.firstOrNull { (age, pair) -> gmtJulDay > pair.first && pair.second > gmtJulDay }?.key
  }

  /**
   * @param count 計算 n柱 大運的資料
   */
  private fun getFortuneDatas(count: Int): List<FortuneData> {
    // forward : 大運是否順行
    val isForward = isFortuneDirectionForward

    //下個大運的干支
    var nextStemBranch = if (isForward) eightWords.month.next else eightWords.month.previous

    val fortuneDatas = ArrayList<FortuneData>(count)

    val gmtJulDay = gmtJulDay

    val ageMap = getAgeMap(120)

    // 計算九柱大運的相關資訊

    for (i in 1..count) {
      // 西元/民國/實歲/虛歲之值
      val startFortune: Int
      val endFortune: Int
      val startFortuneSeconds = getTargetMajorSolarTermsSeconds(i * if (isForward) 1 else -1)
      val endFortuneSeconds = getTargetMajorSolarTermsSeconds((i + 1) * if (isForward) 1 else -1)

      val startFortuneGmtJulDay = gmtJulDay + Math.abs(startFortuneSeconds) * fortuneMonthSpan / 86400.0
      val endFortuneGmtJulDay = gmtJulDay + Math.abs(endFortuneSeconds) * fortuneMonthSpan / 86400.0

      startFortune = getAge(startFortuneGmtJulDay, ageMap) ?: 0
      endFortune = getAge(endFortuneGmtJulDay, ageMap) ?: 0

      val startFortuneAgeNotes = ageNoteImpls.map { impl -> ageMap[startFortune]?.let { impl.getAgeNote(it) } }.filter { it != null }.map { it!! }.toList()
      val endFortuneAgeNotes   = ageNoteImpls.map { impl -> ageMap[  endFortune]?.let { impl.getAgeNote(it) } }.filter { it != null }.map { it!! }.toList()

      val fortuneData = FortuneData(nextStemBranch, startFortuneGmtJulDay, endFortuneGmtJulDay, startFortune, endFortune, startFortuneAgeNotes, endFortuneAgeNotes)
      fortuneDatas.add(fortuneData)

      nextStemBranch = if (isForward) nextStemBranch.next else nextStemBranch.previous
    } // for 1 ~ fortunes)
    return fortuneDatas
  }


  private fun getBranchOf(star: Star): Branch {
    val pos = starPositionImpl.getPosition(star, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return ZodiacSign.getZodiacSign(pos.lng).branch
  }

  /**
   * 由 GMT 反推月大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 月大運干支
   */
  fun getStemBranchOfFortuneMonth(targetGmt: LocalDateTime): StemBranch {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneMonthSpan)
  }

  /**
   * 由 GMT 反推日大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 日大運干支
   */
  fun getStemBranchOfFortuneDay(targetGmt: LocalDateTime): StemBranch {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneDaySpan)
  }

  /**
   * 由 GMT 反推時大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 時大運干支
   */
  fun getStemBranchOfFortuneHour(targetGmt: LocalDateTime): StemBranch {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneHourSpan)
  }

  /**
   * 由 GMT 反推月大運，private method
   *
   * @param targetGmt 目標時刻（必須在出生時刻之後）
   * @param span      放大倍數
   * @return 干支
   */
  private fun getStemBranchOfFortune(targetGmt: LocalDateTime, span: Double): StemBranch {
    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    var resultStemBranch = this.eightWords.month

    val dur = Duration.between(targetGmt, gmt).abs()
    val diffSeconds = dur.seconds + dur.nano / 1_000_000_000.0

    if (targetGmt.isAfter(gmt) && isFortuneDirectionForward) {
      logger.debug("大運順行")
      var index = 1
      while (getTargetMajorSolarTermsSeconds(index) * span < diffSeconds) {
        resultStemBranch = resultStemBranch.next
        index++
      }
      return resultStemBranch
    }
    if (targetGmt.isAfter(gmt) && !isFortuneDirectionForward) {
      logger.debug("大運逆行")
      var index = -1
      while (Math.abs(getTargetMajorSolarTermsSeconds(index) * span) < diffSeconds) {
        resultStemBranch = resultStemBranch.previous
        index--
      }
      return resultStemBranch
    } else
      throw RuntimeException("Error while getStemBranchOfFortune($targetGmt)")
  }


  override fun toString(): String {
    return "EightWordsPersonContext [gender=$gender, lmt=$lmt, location=$location]"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other)
      return true
    if (other !is PersonContext)
      return false

    val that = other as PersonContext?

    if (java.lang.Double.compare(that!!.fortuneMonthSpan, fortuneMonthSpan) != 0)
      return false
    if (java.lang.Double.compare(that.fortuneDaySpan, fortuneDaySpan) != 0)
      return false
    if (java.lang.Double.compare(that.fortuneHourSpan, fortuneHourSpan) != 0)
      return false
    if (solarTermsImpl != that.solarTermsImpl)
      return false
    if (starTransitImpl != that.starTransitImpl)
      return false
    if (lmt != that.lmt)
      return false
    if (location != that.location)
      return false
    if (gender !== that.gender)
      return false
    if (eightWords != that.eightWords)
      return false
    return if (currentSolarTerms != that.currentSolarTerms) false else !if (fortuneDirectionImpl != null) fortuneDirectionImpl != that.fortuneDirectionImpl else that.fortuneDirectionImpl != null

  }

  override fun hashCode(): Int {
    var result: Int
    var temp: Long
    result = solarTermsImpl.hashCode()
    result = 31 * result + starTransitImpl.hashCode()
    result = 31 * result + lmt.hashCode()
    result = 31 * result + location.hashCode()
    result = 31 * result + gender.hashCode()
    result = 31 * result + eightWords.hashCode()
    result = 31 * result + currentSolarTerms.hashCode()
    temp = java.lang.Double.doubleToLongBits(fortuneMonthSpan)
    result = 31 * result + (temp xor temp.ushr(32)).toInt()
    temp = java.lang.Double.doubleToLongBits(fortuneDaySpan)
    result = 31 * result + (temp xor temp.ushr(32)).toInt()
    temp = java.lang.Double.doubleToLongBits(fortuneHourSpan)
    result = 31 * result + (temp xor temp.ushr(32)).toInt()
    result = 31 * result + (fortuneDirectionImpl?.hashCode() ?: 0)
    return result
  }


}
