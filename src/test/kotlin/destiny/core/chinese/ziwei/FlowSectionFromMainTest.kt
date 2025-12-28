/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender.F
import destiny.core.Gender.M
import destiny.core.chinese.YinYang.陰
import destiny.core.chinese.YinYang.陽
import destiny.core.chinese.ziwei.House.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowSectionFromMainTest {

  internal var impl: IFlowSection = FlowSectionFromMain()
  private var seq: IHouseSeq = HouseSeqDefaultImpl()

  @Test
  fun getVageRange() {
    // 陽男順行 , 可參考 https://imgur.com/a/g3D9X
    assertEquals(Pair(2, 11), impl.getAgeRange(命宮, 2, 陽, M, seq))
    assertEquals(Pair(12, 21), impl.getAgeRange(父母, 2, 陽, M, seq))
    assertEquals(Pair(112,121) , impl.getAgeRange(兄弟, 2, 陽, M, seq))


    // 陰女順行
    assertEquals(Pair(2, 11), impl.getAgeRange(命宮, 2, 陰, F, seq))
    assertEquals(Pair(12, 21), impl.getAgeRange(父母, 2, 陰, F, seq))

    // 陰男逆行
    assertEquals(Pair(2, 11), impl.getAgeRange(命宮, 2, 陰, M, seq))
    assertEquals(Pair(12, 21), impl.getAgeRange(兄弟, 2, 陰, M, seq))

    // 陽女逆行
    assertEquals(Pair(2, 11), impl.getAgeRange(命宮, 2, 陽, F, seq))
    assertEquals(Pair(12, 21), impl.getAgeRange(兄弟, 2, 陽, F, seq))
  }

}
