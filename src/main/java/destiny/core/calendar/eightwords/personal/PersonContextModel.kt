/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.IEightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IPersonContextModel : IEightWordsContextModel {
  /** 性別 */
  val gender: Gender

  /** 總共要輸出的大運  */
  val fortuneDataLarges: List<FortuneData>

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<Double, Double>>

  /**
   * 由 GMT 反推月大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 月大運干支
   */
  fun getStemBranchOfFortuneMonth(targetGmt: ChronoLocalDateTime<*>): StemBranch? {
    val gmtJulDay = TimeTools.getGmtJulDay(targetGmt)

    return if (gmtJulDay < fortuneDataLarges[0].startFortuneGmtJulDay)
      eightWords.month // 還未上運 ，傳回 月干支
    else fortuneDataLarges.firstOrNull {
      gmtJulDay > it.startFortuneGmtJulDay &&
        it.endFortuneGmtJulDay > gmtJulDay
    }?.stemBranch
  }
}

/**
 * 順推大運 , 取得該命盤的幾條大運
 */
interface IFortuneLarge {

  /** 順推大運 , 取得該命盤的幾條大運 */
  fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                         location: ILocation,
                         gender: Gender,
                         count: Int): List<FortuneData>
}


/**
 * 逆推大運，
 * 由 GMT 反推 大運 是哪條干支
 */
interface IReverseFortuneLarge {


  /**
   * 由 GMT 反推月大運
   * @param targetGmt 目標時刻為此時， 計算此時刻是屬於哪條月大運當中
   * 實際會與 [IPersonContextModel.getStemBranchOfFortuneMonth] 結果相同
   * */
  fun getStemBranchOfFortuneMonth(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  targetGmt: ChronoLocalDateTime<*>): StemBranch
}

/**
 * 利用星體運算「倍數延展」的方式計算、反推大運 的實作
 */
interface IReverseFortuneLargeSpan : IReverseFortuneLarge {

  /** 運 :「月」的 span 倍數，通常為 120 ，即：一個月干支 擴展(乘以)120 倍，變成十年  */
  val fortuneMonthSpan: Double

  /** 運：「日」的 span 倍數，通常為 365，即：一日走一年  */
  val fortuneDaySpan: Double

  /** 運 :「時辰」的 span 倍數，通常為 365x12，即：一時辰走一年  */
  val fortuneHourSpan: Double

  /**
   * 由 GMT 反推大運
   *
   * @param lmt       出生時刻
   * @param location  出生地點
   * @param targetGmt 目標時刻（必須在出生時刻之後）
   * @param span      放大倍數
   * @return 干支
   */
  fun getStemBranchOfFortune(lmt: ChronoLocalDateTime<*>,
                             location: ILocation,
                             gender: Gender,
                             targetGmt: ChronoLocalDateTime<*>,
                             span: Double): StemBranch

  /** 由 GMT 反推月大運 */
  override fun getStemBranchOfFortuneMonth(lmt: ChronoLocalDateTime<*>,
                                           location: ILocation,
                                           gender: Gender,
                                           targetGmt: ChronoLocalDateTime<*>): StemBranch {
    return getStemBranchOfFortune(lmt, location, gender, targetGmt, fortuneMonthSpan)
  }
}

/**
 * 類似 [IEightWordsContext]
 * 提供純粹「時間、地點、性別」的切入點 , 不帶其他參數，取得一張個人命盤
 */
interface IPersonContext : IEightWordsContext , IFortuneLarge {


  fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                            location: ILocation,
                            place: String?,
                            gender: Gender): IPersonContextModel

  val ageNoteImpls: List<IntAgeNote>
}

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
data class PersonContextModel(

  private val eightWordsContextModel: IEightWordsContextModel,

  /** 性別 */
  override val gender: Gender,

  /** 總共要輸出的大運  */
  override val fortuneDataLarges: List<FortuneData>,

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  override val ageMap: Map<Int, Pair<Double, Double>>) : IPersonContextModel,
  IEightWordsContextModel by eightWordsContextModel, Serializable

/** 除了「人」的資料，還包括「排盤當下的時間」，會標註當下行運、流年 */
interface IPersonPresentModel : IPersonContextModel {

  val viewGmt: ChronoLocalDateTime<*>

  val viewChineseDate: ChineseDate

  /** 此時處於何大運中 */
  val selectedFortuneData: FortuneData

  /** 此時處於何歲中 */
  val selectedAge: Int
}

data class PersonPresentModel(
  private val personContextModel: PersonContextModel,
  override val viewGmt: ChronoLocalDateTime<*>,
  override val viewChineseDate: ChineseDate,
  override val selectedFortuneData: FortuneData,
  override val selectedAge: Int) : IPersonPresentModel, IPersonContextModel by personContextModel, Serializable
