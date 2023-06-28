/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.Branch.*
import destiny.core.chinese.liuren.General.*
import kotlin.test.Test
import kotlin.test.assertSame

class GeneralStemBranchLiurenTest {

  @Test
  fun get() {
    val impl = GeneralStemBranchLiuren()
    assertSame(貴人, impl[丑])
    assertSame(螣蛇, impl[巳])
    assertSame(朱雀, impl[午])
    assertSame(六合, impl[卯])
    assertSame(勾陳, impl[辰])
    assertSame(青龍, impl[寅])
    assertSame(天空, impl[戌])
    assertSame(白虎, impl[申])
    assertSame(太常, impl[未])
    assertSame(玄武, impl[亥])
    assertSame(太陰, impl[酉])
    assertSame(天后, impl[子])
  }

}