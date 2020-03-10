/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.liuren.General.*
import kotlin.test.Test
import kotlin.test.assertSame

class GeneralZhaoSeqImplTest {

  private val seqZhao = GeneralSeqZhaoImpl()

  @Test
  operator fun next() {
    assertSame(青龍, 青龍.next(-24, seqZhao))
    assertSame(青龍, 青龍.next(-12, seqZhao))
    assertSame(六合, 青龍.next(-11, seqZhao))
    assertSame(天后, 青龍.next(-2, seqZhao))
    assertSame(貴人, 青龍.next(-1, seqZhao))
    assertSame(青龍, 青龍.next(0, seqZhao))
    assertSame(六合, 青龍.next(1, seqZhao))
    assertSame(勾陳, 青龍.next(2, seqZhao))
    assertSame(貴人, 青龍.next(11, seqZhao))
    assertSame(青龍, 青龍.next(12, seqZhao))
    assertSame(青龍, 青龍.next(24, seqZhao))
  }

  @Test
  fun prev() {
    assertSame(青龍, 青龍.prev(24, seqZhao))
    assertSame(青龍, 青龍.prev(12, seqZhao))
    assertSame(六合, 青龍.prev(11, seqZhao))
    assertSame(天后, 青龍.prev(2, seqZhao))
    assertSame(貴人, 青龍.prev(1, seqZhao))
    assertSame(青龍, 青龍.prev(0, seqZhao))
    assertSame(六合, 青龍.prev(-1, seqZhao))
    assertSame(勾陳, 青龍.prev(-2, seqZhao))
    assertSame(貴人, 青龍.prev(-11, seqZhao))
    assertSame(青龍, 青龍.prev(-12, seqZhao))
    assertSame(青龍, 青龍.prev(-24, seqZhao))
  }
}
