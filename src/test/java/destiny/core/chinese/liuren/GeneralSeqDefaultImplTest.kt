/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.Branch.*
import destiny.core.chinese.liuren.General.*
import kotlin.test.Test
import kotlin.test.assertSame

class GeneralSeqDefaultImplTest {

  private val seqDefault = GeneralSeqDefaultImpl()

  @Test
  fun testGet() {

    val liuren = GeneralStemBranchLiuren()

    assertSame(貴人, General.get(丑, liuren))
    assertSame(螣蛇, General.get(巳, liuren))
    assertSame(朱雀, General.get(午, liuren))
    assertSame(六合, General.get(卯, liuren))
    assertSame(勾陳, General.get(辰, liuren))
    assertSame(青龍, General.get(寅, liuren))
    assertSame(天空, General.get(戌, liuren))
    assertSame(白虎, General.get(申, liuren))
    assertSame(太常, General.get(未, liuren))
    assertSame(玄武, General.get(亥, liuren))
    assertSame(太陰, General.get(酉, liuren))
    assertSame(天后, General.get(子, liuren))
  }

  @Test
  fun testNext() {
    assertSame(貴人, 貴人.next(-12, seqDefault))
    assertSame(螣蛇, 貴人.next(-11, seqDefault))
    assertSame(太陰, 貴人.next(-2, seqDefault))
    assertSame(天后, 貴人.next(-1, seqDefault))
    assertSame(貴人, 貴人.next(0, seqDefault))
    assertSame(螣蛇, 貴人.next(1, seqDefault))
    assertSame(朱雀, 貴人.next(2, seqDefault))
    assertSame(天后, 貴人.next(11, seqDefault))
    assertSame(貴人, 貴人.next(12, seqDefault))
    assertSame(螣蛇, 貴人.next(13, seqDefault))
  }

  @Test
  fun testPrev() {
    assertSame(貴人, 貴人.prev(-12, seqDefault))
    assertSame(天后, 貴人.prev(-11, seqDefault))
    assertSame(朱雀, 貴人.prev(-2, seqDefault))
    assertSame(螣蛇, 貴人.prev(-1, seqDefault))
    assertSame(貴人, 貴人.prev(0, seqDefault))
    assertSame(天后, 貴人.prev(1, seqDefault))
    assertSame(太陰, 貴人.prev(2, seqDefault))
    assertSame(螣蛇, 貴人.prev(11, seqDefault))
    assertSame(貴人, 貴人.prev(12, seqDefault))
    assertSame(天后, 貴人.prev(13, seqDefault))
  }
}