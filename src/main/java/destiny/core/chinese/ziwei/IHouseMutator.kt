/**
 * Created by smallufo on 2022-06-19.
 */
package destiny.core.chinese.ziwei

/**
 * 命盤變盤
 */
interface IHouseMutator {

  /** 有修改過才傳回，否則傳回 null , 意味沒有修改過 */
  fun mutate(plate: IPlate, config: ZiweiConfig): Set<HouseData>?

}
