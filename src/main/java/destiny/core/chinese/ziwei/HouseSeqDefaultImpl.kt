/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_HOUSE_SEQ

/** 南派  */
@Impl([Domain(KEY_HOUSE_SEQ, HouseSeqDefaultImpl.VALUE, default = true)])
class HouseSeqDefaultImpl : HouseSeqAbstractImpl() {

  override val houseSeq: HouseSeq = HouseSeq.Default

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
    const val VALUE = "default"
    private val ARRAY = arrayOf(命宮, 兄弟, 夫妻, 子女, 財帛, 疾厄, 遷移, 交友, 官祿, 田宅, 福德, 父母)
  }

}
