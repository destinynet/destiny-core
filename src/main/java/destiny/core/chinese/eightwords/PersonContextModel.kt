/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.chinese.eightwords

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/** 八字 (包含 人 的資料) */
interface IPersonContextModel : IEightWordsContextModel , IBirthDataNamePlace  {

  /** 總共要輸出的大運  */
  val fortuneDataLarges: List<FortuneData>

  /** 小運 */
  val fortuneDataSmalls: List<FortuneData>

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>

  /**
   * 由 GMT 反推月大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 月大運干支
   */
  fun getStemBranchOfFortuneMonth(targetGmt: ChronoLocalDateTime<*>): IStemBranch? {
    val gmtJulDay = TimeTools.getGmtJulDay(targetGmt)

    return if (gmtJulDay < fortuneDataLarges[0].startFortuneGmtJulDay)
      eightWords.month // 還未上運 ，傳回 月干支
    else fortuneDataLarges.firstOrNull {
      gmtJulDay > it.startFortuneGmtJulDay &&
        it.endFortuneGmtJulDay > gmtJulDay
    }?.stemBranch
  }
}


/** 推算 大運 演算法 */
interface IPersonFortuneLarge : Descriptive {

  /** 順推大運 , 取得該命盤的幾條大運 */
  fun getFortuneDataList(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, count: Int, config: FortuneLargeConfig): List<FortuneData>

  /**
   * 逆推大運
   * 由 GMT 反推月大運
   * @param targetGmt 目標時刻為此時， 計算此時刻是屬於哪條月大運當中
   * 實際會與 [IPersonContextModel.getStemBranchOfFortuneMonth] 結果相同
   * */
  fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, targetGmt: ChronoLocalDateTime<*>, config: FortuneLargeConfig): IStemBranch
}


/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
data class PersonContextModel(

  private val eightWordsContextModel: IEightWordsContextModel,

  /** 性別 */
  override val gender: Gender,

  /** 名稱 */
  override val name: String?,

  /** 總共要輸出的大運  */
  override val fortuneDataLarges: List<FortuneData>,

  /** 小運 */
  override val fortuneDataSmalls: List<FortuneData>,

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  override val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>) : IPersonContextModel,
  IEightWordsContextModel by eightWordsContextModel, Serializable

/** 除了「人」的資料，還包括「排盤當下的時間」，會標註當下行運、流年 */
interface IPersonPresentModel : IPersonContextModel {

  val viewGmt: ChronoLocalDateTime<*>

  val viewChineseDate: ChineseDate

  /** 目前所處於的大運 */
  val selectedFortuneLarge: IStemBranch

  /** 承上 , 十年流年 */
  val selectedFortuneLargeYears: List<StemBranch>

  /** 當年流年 */
  val presentYear: StemBranch

}

data class PersonPresentModel(
  private val personContextModel: IPersonContextModel,
  override val viewGmt: ChronoLocalDateTime<*>,
  override val viewChineseDate: ChineseDate,
  override val selectedFortuneLarge: IStemBranch,
  override val selectedFortuneLargeYears: List<StemBranch>,
  override val presentYear: StemBranch) :
  IPersonPresentModel, IPersonContextModel by personContextModel, Serializable

