/**
 * Created by smallufo on 2018-02-04.
 */
package destiny.iching.divine

import destiny.iching.Hexagram
import kotlin.test.Test
import kotlin.test.assertSame

class DivinesTest {

  @Test
  fun `乾為天 之 乾為天`() {
    val plate = Divines.getPlate(Hexagram.乾 , Hexagram.乾)
    assertSame(1 , plate.本卦宮序)
    assertSame(1 , plate.變卦宮序)
    assertSame(6 , plate.本卦世爻)
    assertSame(3 , plate.本卦應爻)
    println("plate = $plate")
  }

  @Test
  fun `乾為天 之 天風姤`() {
    val plate = Divines.getPlate(Hexagram.乾 , Hexagram.姤)
    assertSame(1 , plate.本卦宮序)
    assertSame(2 , plate.變卦宮序)

    assertSame(1 , plate.變卦世爻)
    assertSame(4 , plate.變卦應爻)
    println("plate = $plate")
  }


  @Test
  fun `乾為天 之 火天大有`() {
    val plate = Divines.getPlate(Hexagram.乾 , Hexagram.大有)
    assertSame(1 , plate.本卦宮序)
    assertSame(8 , plate.變卦宮序)

    assertSame(3 , plate.變卦世爻)
    assertSame(6 , plate.變卦應爻)
    println("plate = $plate")
  }
}