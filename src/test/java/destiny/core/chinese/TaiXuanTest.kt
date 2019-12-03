/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import kotlin.test.Test
import kotlin.test.assertSame

class TaiXuanTest {

  @Test
  fun testGetStem() {
    assertSame(9 , TaiXuan[Stem.甲])
    assertSame(5 , TaiXuan[Stem.戊])
    assertSame(5 , TaiXuan[Stem.癸])
  }

  @Test
  fun testGetBranch() {
    assertSame(9 , TaiXuan[Branch.子])
    assertSame(5 , TaiXuan[Branch.辰])
    assertSame(4 , TaiXuan[Branch.亥])
  }
}
