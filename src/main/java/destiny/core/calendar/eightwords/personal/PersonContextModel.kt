/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.IEightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.core.chinese.IStemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IPersonContextModel : IEightWordsContextModel {
  /** 性別 */
  val gender: Gender

  /** 名稱 */
  val name: String?

  /** 總共要輸出的大運  */
  val fortuneDataLarges: List<FortuneData>

  /** 小運 */
  val fortuneDataSmalls: List<FortuneData>

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<Double, Double>>

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

  /**
   * 順推大運
   * 取得該命盤的幾條大運 */
  fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                         location: ILocation,
                         gender: Gender,
                         count: Int): List<FortuneData>

  /**
   * 逆推大運
   * 由 GMT 反推月大運
   * @param targetGmt 目標時刻為此時， 計算此時刻是屬於哪條月大運當中
   * 實際會與 [IPersonContextModel.getStemBranchOfFortuneMonth] 結果相同
   * */
  fun getStemBranch(lmt: ChronoLocalDateTime<*>,
                    location: ILocation,
                    gender: Gender,
                    targetGmt: ChronoLocalDateTime<*>): IStemBranch
}


/** 推算小運 */
interface IPersonFortuneSmall {

  /**
   * 順推小運
   * 取得幾條小運
   */
  fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                         location: ILocation,
                         gender: Gender,
                         count: Int): List<FortuneData>
}

/**
 * 類似 [IEightWordsContext]
 * 提供純粹「時間、地點、性別」的切入點 , 不帶其他參數，取得一張個人命盤
 */
interface IPersonContext : IEightWordsContext {


  fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                            location: ILocation,
                            place: String?,
                            gender: Gender,
                            name: String?): IPersonContextModel

  fun getPersonContextModel(data: BirthDataNamePlace): IPersonContextModel {
    return getPersonContextModel(data.time, data.location, data.place, data.gender, data.name)
  }

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

  /** 名稱 */
  override val name: String?,

  /** 總共要輸出的大運  */
  override val fortuneDataLarges: List<FortuneData>,

  /** 小運 */
  override val fortuneDataSmalls: List<FortuneData>,

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  override val ageMap: Map<Int, Pair<Double, Double>>) : IPersonContextModel,
  IEightWordsContextModel by eightWordsContextModel, Serializable

/** 除了「人」的資料，還包括「排盤當下的時間」，會標註當下行運、流年 */
interface IPersonPresentModel : IPersonContextModel {

  val viewGmt: ChronoLocalDateTime<*>

  val viewChineseDate: ChineseDate

  /** 目前所處於的大運 */
  val selectedFortuneLarge: IStemBranch

}

data class PersonPresentModel(
  private val personContextModel: IPersonContextModel,
  override val viewGmt: ChronoLocalDateTime<*>,
  override val viewChineseDate: ChineseDate,
  override val selectedFortuneLarge: IStemBranch) :
  IPersonPresentModel, IPersonContextModel by personContextModel, Serializable

interface IPersonPresentContext : IPersonContext {

  fun getPersonPresentModel(lmt: ChronoLocalDateTime<*>,
                            location: ILocation,
                            place: String?,
                            gender: Gender,
                            name: String?,
                            viewGmt: ChronoLocalDateTime<*>): IPersonPresentModel

  fun getPersonPresentModel(data: IBirthDataNamePlace,
                            viewGmt: ChronoLocalDateTime<*>): IPersonPresentModel {
    return getPersonPresentModel(data.time, data.location, data.place, data.gender, data.name, viewGmt)
  }
}