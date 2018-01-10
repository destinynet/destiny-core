package destiny.iching.mume

import destiny.iching.Hexagram
import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class MumeContextTest {

  @Test
  fun testGetTargetHexagram() {
    assertEquals(Hexagram.姤, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 1).targetHexagram)
    assertEquals(Hexagram.同人, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 2).targetHexagram)
    assertEquals(Hexagram.履, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 3).targetHexagram)
    assertEquals(Hexagram.小畜, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 4).targetHexagram)
    assertEquals(Hexagram.大有, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 5).targetHexagram)
    assertEquals(Hexagram.夬, MumeContext(Hexagram.getHexagram(Symbol.乾, Symbol.乾), 6).targetHexagram)

    assertEquals(Hexagram.復, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 1).targetHexagram)
    assertEquals(Hexagram.師, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 2).targetHexagram)
    assertEquals(Hexagram.謙, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 3).targetHexagram)
    assertEquals(Hexagram.豫, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 4).targetHexagram)
    assertEquals(Hexagram.比, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 5).targetHexagram)
    assertEquals(Hexagram.剝, MumeContext(Hexagram.getHexagram(Symbol.坤, Symbol.坤), 6).targetHexagram)

  }
}