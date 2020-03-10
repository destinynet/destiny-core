/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*
import destiny.tools.Domain
import destiny.tools.Impl

/**
 * 太乙
 * 相對於紫微斗數全書 的系統
 *
 * 多了 [House.相貌] , 但少了 [House.遷移]
 */
@Impl([Domain("houseSeq", HouseSeqTaiyiImpl.VALUE)])
class HouseSeqTaiyiImpl : HouseSeqAbstractImpl() {

  override val houses: Array<House>
    get() = ARRAY

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "taiyi"
    private val ARRAY = arrayOf(命宮, 兄弟, 夫妻, 子女, 財帛, 田宅, 官祿, 交友, 疾厄, 福德, 相貌, 父母)
  }

}
