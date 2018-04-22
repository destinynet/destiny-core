/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWordsContextModel
import destiny.core.calendar.eightwords.IEightWordsContextModel
import java.io.Serializable
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime

interface IPersonContextModel : IEightWordsContextModel {
  /** 性別 */
  val gender: Gender

  /** 總共要輸出的大運  */
  val fortuneDatas: List<FortuneData>

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<Double, Double>>
}

/**
 * 類似 [IEightWordsContext]
 * 提供純粹「時間、地點、性別」的切入點 , 不帶其他參數，取得一張個人命盤
 * 未來可用以取代 [PersonContext]
 */
interface IPersonContext {

  fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                            location: ILocation,
                            place: String?,
                            gender: Gender): IPersonContextModel
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
  override val fortuneDatas: List<FortuneData>,

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  override val ageMap: Map<Int, Pair<Double, Double>>) : IPersonContextModel,
  IEightWordsContextModel by eightWordsContextModel, Serializable

/** 除了「人」的資料，還包括「排盤當下的時間」，會標註當下行運、流年 */
interface IPersonPresentModel : IPersonContextModel {

  val viewGmt: LocalDateTime

  val viewChineseDate: ChineseDate

  /** 此時處於何大運中 */
  val selectedFortuneData: FortuneData

  /** 此時處於何歲中 */
  val selectedAge: Int
}

data class PersonPresentModel(
  private val personContextModel: PersonContextModel,
  override val viewGmt: LocalDateTime,
  override val viewChineseDate: ChineseDate,
  override val selectedFortuneData: FortuneData,
  override val selectedAge: Int) : IPersonPresentModel, IPersonContextModel by personContextModel, Serializable
