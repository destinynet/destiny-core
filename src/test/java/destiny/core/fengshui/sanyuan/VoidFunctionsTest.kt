/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.Mountain
import destiny.core.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertSame


class VoidFunctionsTest {

  @Test
  fun testGetMappingMountain() {
    assertSame(
      Mountain.壬, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.坎))
    assertSame(
      Mountain.丑, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.艮))
    assertSame(
      Mountain.甲, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.震))
    assertSame(
      Mountain.辰, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.巽))
    assertSame(
      Mountain.丙, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.離))
    assertSame(
      Mountain.未, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.坤))
    assertSame(
      Mountain.庚, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.兌))
    assertSame(
      Mountain.戌, VoidFunctions.getMappingMountain(
      Mountain.壬, Symbol.乾))
  }
}
