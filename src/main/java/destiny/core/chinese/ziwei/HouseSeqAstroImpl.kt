/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*

class HouseSeqAstroImpl : HouseSeqAbstractImpl() {

  override val houses: Array<House>
    get() = ARRAY



  companion object {

    private val ARRAY = arrayOf(命宮, 財帛, 兄弟, 田宅, 子女, 交友, 夫妻, 疾厄, 遷移, 官祿, 福德, 相貌)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

}
