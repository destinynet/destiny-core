/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.YinYangIF
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BigRangeFromMainTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  internal var impl: IBigRange = BigRangeFromMain()
  private var seq: IHouseSeq = HouseSeqDefaultImpl()

  @Test
  fun testString() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
  }


  @Test
  fun getVageRange() {
    // 陽男順行
    assertEquals(Pair(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陽, Gender.男, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(House.父母, 2, YinYangIF.陽, Gender.男, seq))

    // 陰女順行
    assertEquals(Pair(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陰, Gender.女, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(House.父母, 2, YinYangIF.陰, Gender.女, seq))

    // 陰男逆行
    assertEquals(Pair(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陰, Gender.男, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(House.兄弟, 2, YinYangIF.陰, Gender.男, seq))

    // 陽女逆行
    assertEquals(Pair(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陽, Gender.女, seq))
    assertEquals(Pair(12, 21), impl.getVageRange(House.兄弟, 2, YinYangIF.陽, Gender.女, seq))
  }

}