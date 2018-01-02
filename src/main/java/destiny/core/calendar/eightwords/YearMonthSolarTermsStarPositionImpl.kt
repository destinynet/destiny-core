/**
 * @author smallufo
 * Created on 2006/5/22 at 下午 12:09:24
 */
package destiny.core.calendar.eightwords

import destiny.astrology.Centric.GEO
import destiny.astrology.Coordinate.ECLIPTIC
import destiny.astrology.Coordinate.EQUATORIAL
import destiny.astrology.IStarPosition
import destiny.astrology.IStarTransit
import destiny.astrology.Planet
import destiny.core.calendar.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit
import java.util.function.Function

/**
 * <pre>
 * 年：具備設定換年點的功能
 * <BR></BR>
 * 月：「定氣法」計算地支 , 計算太陽在黃道帶 0 , 15 , 30 ... 345 度的時刻
 * <BR></BR>具備設定 南北半球月令是否對沖﹑界定南北半球的方法（赤道/赤緯度數）
</pre> *
 */
class YearMonthSolarTermsStarPositionImpl : IYearMonth, Serializable {

  private val starPositionImpl: IStarPosition<*>?

  private val starTransitImpl: IStarTransit

  /** 南半球月令是否對沖  */
  private var southernHemisphereOpposition = false

  /** 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球  */
  /**
   * 南半球的判定方法，要依劇緯度 還是 回歸線？
   * <BR></BR> 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   * <BR></BR> 內定是 false
   */
  var hemisphereBy = HemisphereBy.EQUATOR

  /** 換年的度數 , 通常是立春點 (315) 換年 */
  private var changeYearDegree: Double = 0.toDouble()

  private val logger = LoggerFactory.getLogger(javaClass)

  constructor(changeYearDegree: Double, starPositionImpl: IStarPosition<*>, starTransitImpl: IStarTransit) {
    this.starPositionImpl = starPositionImpl
    this.starTransitImpl = starTransitImpl
    this.setChangeYearDegree(changeYearDegree)
  }

  constructor(changeYearDegree: Double, starPositionImpl: IStarPosition<*>, starTransitImpl: IStarTransit, southernHemisphereOpposition: Boolean) {
    this.starPositionImpl = starPositionImpl
    this.starTransitImpl = starTransitImpl
    this.southernHemisphereOpposition = southernHemisphereOpposition
    this.setChangeYearDegree(changeYearDegree)
  }

  private fun setChangeYearDegree(changeYearDegree: Double) {
    if (changeYearDegree < 180)
      throw RuntimeException("Cannot set changeYearDegree smaller than 180 ")
    this.changeYearDegree = changeYearDegree
  }


  override fun getYear(gmtJulDay: Double, loc: Location): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

    val resultStemBranch: StemBranch
    //西元 1984 年為 甲子年
    val index: Int
    if (lmt.get(ChronoField.YEAR) > 0)
      index = (lmt.get(ChronoField.YEAR) - 1984) % 60
    else
      index = (1 - lmt.get(ChronoField.YEAR) - 1984) % 60

    val gmtSecondsOffset = TimeTools.getDstSecondOffset(lmt, loc).v2().toDouble()

    val gmtSecondsOffsetInt = gmtSecondsOffset.toInt()
    val gmtNanoOffset = ((gmtSecondsOffset - gmtSecondsOffsetInt) * 1000000000).toInt()

    val gmt = lmt.minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS).minus(gmtNanoOffset.toLong(), ChronoUnit.NANOS)


    val solarLongitude = starPositionImpl!!.getPosition(Planet.SUN, gmt, GEO, ECLIPTIC).lng
    if (solarLongitude < 180)
    //立春(0)過後，到秋分之間(180)，確定不會換年
      resultStemBranch = StemBranch.get(index)
    else {
      // 360 > solarLongitude >= 180

      //取得 lmt 當年 1/1 凌晨零分的度數
      val startOfYear = lmt.with(DAY_OF_YEAR, 1).with(HOUR_OF_DAY, 0).with(MINUTE_OF_HOUR, 0).minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS)

      val degreeOfStartOfYear = starPositionImpl.getPosition(Planet.SUN, startOfYear, GEO, ECLIPTIC).lng

      if (changeYearDegree >= degreeOfStartOfYear) {
        if (solarLongitude >= changeYearDegree)
          resultStemBranch = StemBranch.get(index)
        else if (changeYearDegree > solarLongitude && solarLongitude >= degreeOfStartOfYear) {
          val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
          if (TimeTools.isBefore(tempTime, startOfYear))
            resultStemBranch = StemBranch.get(index - 1)
          else
            resultStemBranch = StemBranch.get(index)
        } else
          resultStemBranch = StemBranch.get(index)
      } else {
        // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
        if (solarLongitude >= degreeOfStartOfYear) {
          val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
          if (TimeTools.isBefore(tempTime, startOfYear))
            resultStemBranch = StemBranch.get(index)
          else
            resultStemBranch = StemBranch.get(index + 1)
        } else {
          if (solarLongitude >= changeYearDegree)
            resultStemBranch = StemBranch.get(index + 1)
          else
            resultStemBranch = StemBranch.get(index)
        }
      }

    }
    // 儲存年干 , 方便稍後推算月干
    //    this.年干 = resultStemBranch.getStem();
    return resultStemBranch
  }


  /**
   * @return 取得月干支
   */
  override fun getMonth(gmtJulDay: Double, location: Location): StemBranch {
    val result月支: Branch
    //先算出太陽在黃經上的度數

    val solarTermsImpl = SolarTermsImpl(this.starTransitImpl, this.starPositionImpl!!)
    val MonthST = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    var monthIndex = SolarTerms.getIndex(MonthST) / 2 + 2
    if (monthIndex >= 12)
      monthIndex = monthIndex - 12
    val 月支 = Branch.get(monthIndex)

    if (southernHemisphereOpposition) {
      /*
            * 解決南半球月支正沖的問題
            */
      if (hemisphereBy == HemisphereBy.EQUATOR) {
        //如果是依據赤道來區分南北半球
        if (!location.isNorth)
          result月支 = Branch.get(monthIndex + 6)
        else
          result月支 = 月支
      } else {
        /*
                * 如果 hemisphereBy == DECLINATION (赤緯) , 就必須計算 太陽在「赤緯」的度數
                */
        val solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, EQUATORIAL).lat

        if (solarEquatorialDegree >= 0) {
          //如果太陽在赤北緯
          if (location.isNorth) {
            //地點在北半球
            if (location.latitude >= solarEquatorialDegree)
              result月支 = 月支
            else
              result月支 = Branch.get(monthIndex + 6) //所在地緯度低於 太陽赤緯，取對沖月份
          } else {
            //地點在南半球 , 取正沖
            result月支 = Branch.get(monthIndex + 6)
          }
        } else {
          //太陽在赤南緯
          if (!location.isNorth) {
            //地點在南半球
            if (location.latitude <= solarEquatorialDegree)
              result月支 = Branch.get(monthIndex + 6) //所在地緯度高於 太陽赤南緯，真正的南半球
            else
              result月支 = 月支 //雖在南半球，但緯度低於太陽赤南緯，視為北半球
          } else {
            //地點在北半球，月支不變
            result月支 = 月支
          }
        }
      }
    } else
      result月支 = 月支

    val 年干 = getYear(gmtJulDay, location).stem
    return StemBranch.get(this.getMonthStem(gmtJulDay, 年干, result月支), result月支)
  }


  /** 南半球月支是否對沖 , 內定是 '否'  */
  override fun setSouthernHemisphereOpposition(value: Boolean) {
    this.southernHemisphereOpposition = value
  }

  fun isSouthernHemisphereOpposition(): Boolean {
    return southernHemisphereOpposition
  }

  /**
   * 五虎遁月 取得月干
   * <pre>
   * **
   * 甲己之年丙作首
   * 乙庚之歲戊為頭
   * 丙辛之歲由庚上
   * 丁壬壬位順行流
   * 若言戊癸何方發
   * 甲寅之上好追求。
   ** *
  </pre> *
   */
  private fun getMonthStem(gmtJulDay: Double, 年干: Stem, 月支: Branch): Stem {
    var 月干: Stem

    月干 = when (年干) {
      Stem.甲, Stem.己 -> if (月支.index >= 2) Stem.get(Branch.getIndex(月支)) else Stem.get(Branch.getIndex(月支) + 2)
      Stem.乙, Stem.庚 -> if (月支.index >= 2) Stem.get(Branch.getIndex(月支) + 2) else Stem.get(Branch.getIndex(月支) + 4)
      Stem.丙, Stem.辛 -> if (月支.index >= 2) Stem.get(Branch.getIndex(月支) + 4) else Stem.get(Branch.getIndex(月支) + 6)
      Stem.丁, Stem.壬 -> if (月支.index >= 2) Stem.get(Branch.getIndex(月支) + 6) else Stem.get(Branch.getIndex(月支) + 8)
      Stem.戊, Stem.癸 -> if (月支.index >= 2) Stem.get(Branch.getIndex(月支) + 8) else Stem.get(Branch.getIndex(月支) + 10)
    }

    if (changeYearDegree != 315.0) {
      if (starPositionImpl == null)
        throw RuntimeException("Call state error ! starTransitImpl should be set.")

      if (changeYearDegree < 315) {
        logger.debug("換年點在立春前 , changeYearDegree < 315 , value = {}", changeYearDegree)

        val lmtSunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, ECLIPTIC).lng
        if (lmtSunDegree > changeYearDegree && 315 > lmtSunDegree) {
          // t <---立春---- LMT -----換年點
          月干 = Stem.get(月干.index - 2)
        }
      } else if (changeYearDegree > 315) {
        //換年點在立春後 , 還沒測試
        val lmtSunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, ECLIPTIC).lng
        if (lmtSunDegree > 315 && changeYearDegree > lmtSunDegree)
          月干 = Stem.get(月干.index + 2)
      }
    }
    return 月干
  }

  companion object {

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }


}
