/**
 * Created by smallufo on 2017-12-28.
 */
package destiny.astrology

import kotlin.test.Test
import kotlin.test.assertNotNull

//val starNumMap = mapOf(
//  1 to AStar.SUN,
//  2 to AStar.MOON
//)

class AStarTest {

  @Test
  fun fromString() {
    //println(AStarMap.numStarMap)
    //starNumMap
    println("from = " + AStar.fromString("mOOn"))
  }

  @Test
  fun values() {
    for (a in AStar.values) {
      assertNotNull(a)
      println(a)
    }

    val shuffled = AStar.values.asList().shuffled()
    println("shuffled = $shuffled")
    println("sorted = ${shuffled.sorted()}")
  }

}