/**
 * Created by smallufo on 2018-01-29.
 */
package destiny.core.chinese

import kotlin.test.Test
import kotlin.test.assertSame

class SixAnimalTest {

  @Test
  fun next() {
    assertSame(SixAnimal.青龍 , SixAnimal.青龍.next(0))
    assertSame(SixAnimal.朱雀 , SixAnimal.青龍.next(1))
    assertSame(SixAnimal.勾陳 , SixAnimal.青龍.next(2))
    assertSame(SixAnimal.螣蛇 , SixAnimal.青龍.next(3))
    assertSame(SixAnimal.白虎 , SixAnimal.青龍.next(4))
    assertSame(SixAnimal.玄武 , SixAnimal.青龍.next(5))
    assertSame(SixAnimal.青龍 , SixAnimal.青龍.next(6))
  }

  @Test
  fun prev() {
    assertSame(SixAnimal.青龍 , SixAnimal.青龍.prev(0))
    assertSame(SixAnimal.玄武 , SixAnimal.青龍.prev(1))
    assertSame(SixAnimal.白虎 , SixAnimal.青龍.prev(2))
    assertSame(SixAnimal.螣蛇 , SixAnimal.青龍.prev(3))
    assertSame(SixAnimal.勾陳 , SixAnimal.青龍.prev(4))
    assertSame(SixAnimal.朱雀 , SixAnimal.青龍.prev(5))
    assertSame(SixAnimal.青龍 , SixAnimal.青龍.prev(6))
  }
}