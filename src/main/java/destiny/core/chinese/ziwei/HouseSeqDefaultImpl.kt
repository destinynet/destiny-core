/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*

/** 南派  */
class HouseSeqDefaultImpl : HouseSeqAbstractImpl() {

  override val houses: Array<House>
    get() = ARRAY

  companion object {

    private val ARRAY = arrayOf(命宮, 兄弟, 夫妻, 子女, 財帛, 疾厄, 遷移, 交友, 官祿, 田宅, 福德, 父母)
  }

}
