/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender.女
import destiny.core.Gender.男
import destiny.core.chinese.YinYang.陰
import destiny.core.chinese.YinYang.陽
import destiny.core.chinese.ziwei.House.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BigRangeFromMainTest {

  internal var impl: IBigRange = BigRangeFromMain()
  private var seq: IHouseSeq = HouseSeqDefaultImpl()

  @Test
  fun getVageRange() {
    // 陽男順行 , 可參考 https://imgur.com/a/g3D9X
    assertEquals(Pair(2, 11), impl.getVageRange(命宮, 2, 陽, 男, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(父母, 2, 陽, 男, seq))
    assertEquals(Pair(112,121) , impl.getVageRange(兄弟 , 2 , 陽 , 男 , seq))


    // 陰女順行
    assertEquals(Pair(2, 11), impl.getVageRange(命宮, 2, 陰, 女, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(父母, 2, 陰, 女, seq))

    // 陰男逆行
    assertEquals(Pair(2, 11), impl.getVageRange(命宮, 2, 陰, 男, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(兄弟, 2, 陰, 男, seq))

    // 陽女逆行
    assertEquals(Pair(2, 11), impl.getVageRange(命宮, 2, 陽, 女, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(兄弟, 2, 陽, 女, seq))
  }

}
