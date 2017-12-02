/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.YinYangIF
import org.jooq.lambda.tuple.Tuple
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class BigRangeFromMainTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  internal var impl: IBigRange = BigRangeFromMain()
  internal var seq: IHouseSeq = HouseSeqDefaultImpl()

  @Test
  fun testString() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
  }


  @Test
  fun getVageRange() {
    // 陽男順行
    assertEquals(Tuple.tuple(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陽, Gender.男, seq))
    assertEquals(Tuple.tuple(12, 21), impl.getVageRange(House.父母, 2, YinYangIF.陽, Gender.男, seq))

    // 陰女順行
    assertEquals(Tuple.tuple(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陰, Gender.女, seq))
    assertEquals(Tuple.tuple(12, 21), impl.getVageRange(House.父母, 2, YinYangIF.陰, Gender.女, seq))

    // 陰男逆行
    assertEquals(Tuple.tuple(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陰, Gender.男, seq))
    assertEquals(Tuple.tuple(12, 21), impl.getVageRange(House.兄弟, 2, YinYangIF.陰, Gender.男, seq))

    // 陽女逆行
    assertEquals(Tuple.tuple(2, 11), impl.getVageRange(House.命宮, 2, YinYangIF.陽, Gender.女, seq))
    assertEquals(Tuple.tuple(12, 21), impl.getVageRange(House.兄弟, 2, YinYangIF.陽, Gender.女, seq))
  }

}