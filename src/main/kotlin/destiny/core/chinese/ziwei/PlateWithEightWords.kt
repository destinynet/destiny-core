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
  /** 內部原始命盤 (本命或疊盤後的 PlateWith*) , 供 raw [Plate] JSON 序列化取用 */
  val plate: IPlate ,
  /** 八字資料  */
  override val personModel: IPersonContextModel) : IPlateWithEightWords , IPlate by plate , Serializable

