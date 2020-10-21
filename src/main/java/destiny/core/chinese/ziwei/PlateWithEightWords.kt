/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.eightwords.IPersonContextModel
import java.io.Serializable

/**
 * 紫微盤為主，八字盤為輔
 */
interface IPlateWithEightWords : IPlate {
  /** 八字資料  */
  val personModel: IPersonContextModel
}

data class PlateWithEightWords(
  private val plate: IPlate ,
  /** 八字資料  */
  override val personModel: IPersonContextModel) : IPlateWithEightWords , IPlate by plate , Serializable

