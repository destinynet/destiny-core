/**
 * Created by smallufo on 2018-01-29.
 */
package destiny.core.chinese

import destiny.core.chinese.SixAnimal.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SixAnimalTest {

  @Test
  fun next() {
    assertSame(青龍, 青龍.next(0))
    assertSame(朱雀, 青龍.next(1))
    assertSame(勾陳, 青龍.next(2))
    assertSame(螣蛇, 青龍.next(3))
    assertSame(白虎, 青龍.next(4))
    assertSame(玄武, 青龍.next(5))
    assertSame(青龍, 青龍.next(6))
  }

  @Test
  fun prev() {
    assertSame(青龍, 青龍.prev(0))
    assertSame(玄武, 青龍.prev(1))
    assertSame(白虎, 青龍.prev(2))
    assertSame(螣蛇, 青龍.prev(3))
    assertSame(勾陳, 青龍.prev(4))
    assertSame(朱雀, 青龍.prev(5))
    assertSame(青龍, 青龍.prev(6))
  }

  @Test
  fun getSixAnimals() {
    assertEquals(listOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武), SixAnimal.getSixAnimals(Stem.甲))
    assertEquals(listOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武), SixAnimal.getSixAnimals(Stem.乙))
    assertEquals(listOf(朱雀, 勾陳, 螣蛇, 白虎, 玄武, 青龍), SixAnimal.getSixAnimals(Stem.丙))
    assertEquals(listOf(朱雀, 勾陳, 螣蛇, 白虎, 玄武, 青龍), SixAnimal.getSixAnimals(Stem.丁))
    assertEquals(listOf(勾陳, 螣蛇, 白虎, 玄武, 青龍, 朱雀), SixAnimal.getSixAnimals(Stem.戊))
    assertEquals(listOf(螣蛇, 白虎, 玄武, 青龍, 朱雀, 勾陳), SixAnimal.getSixAnimals(Stem.己))
    assertEquals(listOf(白虎, 玄武, 青龍, 朱雀, 勾陳, 螣蛇), SixAnimal.getSixAnimals(Stem.庚))
    assertEquals(listOf(白虎, 玄武, 青龍, 朱雀, 勾陳, 螣蛇), SixAnimal.getSixAnimals(Stem.辛))
    assertEquals(listOf(玄武, 青龍, 朱雀, 勾陳, 螣蛇, 白虎), SixAnimal.getSixAnimals(Stem.壬))
    assertEquals(listOf(玄武, 青龍, 朱雀, 勾陳, 螣蛇, 白虎), SixAnimal.getSixAnimals(Stem.癸))
  }
}
