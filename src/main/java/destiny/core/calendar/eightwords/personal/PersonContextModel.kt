/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.calendar.eightwords.EightWordsContextModel
import destiny.core.calendar.eightwords.IEightWordsContextModel
import java.io.Serializable

interface IPersonContextModel : IEightWordsContextModel {
  /** 性別 */
  val gender: Gender

  /** 總共要輸出的大運  */
  val fortuneDatas: List<FortuneData>

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<Double, Double>>
}

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
data class PersonContextModel(

  private val eightWordsContextModel: EightWordsContextModel,

  /** 性別 */
  override val gender: Gender,

  /** 總共要輸出的大運  */
  override val fortuneDatas: List<FortuneData>,

  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  override val ageMap: Map<Int, Pair<Double, Double>>) : IPersonContextModel,
  IEightWordsContextModel by eightWordsContextModel, Serializable
