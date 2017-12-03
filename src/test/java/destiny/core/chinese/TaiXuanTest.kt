/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import kotlin.test.Test

class TaiXuanTest {

  @Test
  fun testGetStem() {
    for (stem in Stem.values()) {
      println(stem.toString() + " -> " + TaiXuan[stem])
    }
  }

  @Test
  fun testGetBranch() {
    for (branch in Branch.values())
      println(branch.toString() + " -> " + TaiXuan[branch])
  }
}