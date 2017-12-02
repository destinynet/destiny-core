/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.House.*
import org.junit.Assert.*
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class HouseSeqTaiyiImplTest {

  private val impl = HouseSeqTaiyiImpl()

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testString() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  operator fun next() {
    assertSame(官祿, 官祿.next(-24, impl))
    assertSame(官祿, 官祿.next(-12, impl))
    assertSame(交友, 官祿.next(-11, impl))
    assertSame(財帛, 官祿.next(-2, impl))
    assertSame(田宅, 官祿.next(-1, impl))
    assertSame(官祿, 官祿.next(0, impl))
    assertSame(交友, 官祿.next(1, impl))
    assertSame(田宅, 官祿.next(11, impl))
    assertSame(官祿, 官祿.next(12, impl))
    assertSame(官祿, 官祿.next(24, impl))
  }

  @Test
  fun prev() {
    assertSame(官祿, 官祿.prev(-24, impl))
    assertSame(官祿, 官祿.prev(-12, impl))
    assertSame(田宅, 官祿.prev(-11, impl))
    assertSame(疾厄, 官祿.prev(-2, impl))
    assertSame(交友, 官祿.prev(-1, impl))
    assertSame(官祿, 官祿.prev(0, impl))
    assertSame(田宅, 官祿.prev(1, impl))
    assertSame(財帛, 官祿.prev(2, impl))
    assertSame(交友, 官祿.prev(11, impl))
    assertSame(官祿, 官祿.prev(12, impl))
    assertSame(官祿, 官祿.prev(24, impl))
  }

  @Test
  fun getAheadOf() {
    assertEquals(0, impl.getAheadOf(官祿, 官祿).toLong())
    assertEquals(1, impl.getAheadOf(交友, 官祿).toLong())
    assertEquals(2, impl.getAheadOf(疾厄, 官祿).toLong())
    assertEquals(3, impl.getAheadOf(福德, 官祿).toLong())
    assertEquals(4, impl.getAheadOf(相貌, 官祿).toLong())
    assertEquals(5, impl.getAheadOf(父母, 官祿).toLong())
    assertEquals(6, impl.getAheadOf(命宮, 官祿).toLong())
    assertEquals(7, impl.getAheadOf(兄弟, 官祿).toLong())
    assertEquals(8, impl.getAheadOf(夫妻, 官祿).toLong())
    assertEquals(9, impl.getAheadOf(子女, 官祿).toLong())
    assertEquals(10, impl.getAheadOf(財帛, 官祿).toLong())
    assertEquals(11, impl.getAheadOf(田宅, 官祿).toLong())
    assertEquals(0, impl.getAheadOf(官祿, 官祿).toLong())

    assertEquals(-1, impl.getAheadOf(遷移, 官祿).toLong()) // 太乙派 ，不存在 遷移宮
    assertEquals(-1, impl.getAheadOf(官祿, 遷移).toLong()) // 太乙派 ，不存在 遷移宮
    assertEquals(-1, impl.getAheadOf(遷移, 命宮).toLong()) // 太乙派 ，不存在 遷移宮
    assertEquals(-1, impl.getAheadOf(命宮, 遷移).toLong()) // 太乙派 ，不存在 遷移宮
  }

}