/**
 * Created by smallufo on 2018-01-29.
 */
package destiny.core.chinese

import destiny.core.chinese.SixAnimal.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SixAnimalsTest {

  @Test
  fun getSixAnimals() {
    assertEquals(listOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武), SixAnimals.getSixAnimals(Stem.甲))
    assertEquals(listOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武), SixAnimals.getSixAnimals(Stem.乙))
    assertEquals(listOf(朱雀, 勾陳, 螣蛇, 白虎, 玄武, 青龍), SixAnimals.getSixAnimals(Stem.丙))
    assertEquals(listOf(朱雀, 勾陳, 螣蛇, 白虎, 玄武, 青龍), SixAnimals.getSixAnimals(Stem.丁))
    assertEquals(listOf(勾陳, 螣蛇, 白虎, 玄武, 青龍, 朱雀), SixAnimals.getSixAnimals(Stem.戊))
    assertEquals(listOf(螣蛇, 白虎, 玄武, 青龍, 朱雀, 勾陳), SixAnimals.getSixAnimals(Stem.己))
    assertEquals(listOf(白虎, 玄武, 青龍, 朱雀, 勾陳, 螣蛇), SixAnimals.getSixAnimals(Stem.庚))
    assertEquals(listOf(白虎, 玄武, 青龍, 朱雀, 勾陳, 螣蛇), SixAnimals.getSixAnimals(Stem.辛))
    assertEquals(listOf(玄武, 青龍, 朱雀, 勾陳, 螣蛇, 白虎), SixAnimals.getSixAnimals(Stem.壬))
    assertEquals(listOf(玄武, 青龍, 朱雀, 勾陳, 螣蛇, 白虎), SixAnimals.getSixAnimals(Stem.癸))

  }
}