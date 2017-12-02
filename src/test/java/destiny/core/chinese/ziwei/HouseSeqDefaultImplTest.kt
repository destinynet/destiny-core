/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*
import org.junit.Assert.*
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class HouseSeqDefaultImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  internal var impl: IHouseSeq = HouseSeqDefaultImpl()


  @Test
  fun testString() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  operator fun next() {
    assertSame(命宮, 命宮.next(-36, impl))
    assertSame(命宮, 命宮.next(-24, impl))
    assertSame(命宮, 命宮.next(-12, impl))
    assertSame(兄弟, 命宮.next(-11, impl))
    assertSame(福德, 命宮.next(-2, impl))
    assertSame(父母, 命宮.next(-1, impl))
    assertSame(命宮, 命宮.next(0, impl))
    assertSame(兄弟, 命宮.next(1, impl))
    assertSame(父母, 命宮.next(11, impl))
    assertSame(命宮, 命宮.next(12, impl))
    assertSame(命宮, 命宮.next(24, impl))
  }

  @Test
  fun prev() {
    assertSame(命宮, 命宮.prev(-36, impl))
    assertSame(命宮, 命宮.prev(-24, impl))
    assertSame(命宮, 命宮.prev(-12, impl))
    assertSame(父母, 命宮.prev(-11, impl))
    assertSame(夫妻, 命宮.prev(-2, impl))
    assertSame(兄弟, 命宮.prev(-1, impl))
    assertSame(命宮, 命宮.prev(0, impl))
    assertSame(父母, 命宮.prev(1, impl))
    assertSame(福德, 命宮.prev(2, impl))
    assertSame(兄弟, 命宮.prev(11, impl))
    assertSame(命宮, 命宮.prev(12, impl))
    assertSame(命宮, 命宮.prev(24, impl))
  }

  @Test
  fun getAheadOf() {
    assertEquals(0, impl.getAheadOf(命宮, 命宮).toLong())
    assertEquals(1, impl.getAheadOf(兄弟, 命宮).toLong())
    assertEquals(2, impl.getAheadOf(夫妻, 命宮).toLong())
    assertEquals(3, impl.getAheadOf(子女, 命宮).toLong())
    assertEquals(4, impl.getAheadOf(財帛, 命宮).toLong())
    assertEquals(5, impl.getAheadOf(疾厄, 命宮).toLong())
    assertEquals(6, impl.getAheadOf(遷移, 命宮).toLong())
    assertEquals(7, impl.getAheadOf(交友, 命宮).toLong())
    assertEquals(8, impl.getAheadOf(官祿, 命宮).toLong())
    assertEquals(9, impl.getAheadOf(田宅, 命宮).toLong())
    assertEquals(10, impl.getAheadOf(福德, 命宮).toLong())
    assertEquals(11, impl.getAheadOf(父母, 命宮).toLong())

    assertEquals(-1, impl.getAheadOf(相貌, 命宮).toLong()) // 全書派，不存在 相貌宮
    assertEquals(-1, impl.getAheadOf(命宮, 相貌).toLong()) // 全書派，不存在 相貌宮
    assertEquals(-1, impl.getAheadOf(相貌, 兄弟).toLong()) // 全書派，不存在 相貌宮
    assertEquals(-1, impl.getAheadOf(兄弟, 相貌).toLong()) // 全書派，不存在 相貌宮
  }


}