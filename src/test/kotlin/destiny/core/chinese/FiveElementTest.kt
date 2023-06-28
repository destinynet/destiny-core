/**
 * @author smallufo
 * Created on 2005/7/2 at 上午 06:11:30
 */
package destiny.core.chinese

import destiny.core.chinese.FiveElement.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FiveElementTest {
  @Test
  fun testToString() {
    assertEquals(木.toString(), "木")
    assertEquals(火.toString(), "火")
    assertEquals(土.toString(), "土")
    assertEquals(金.toString(), "金")
    assertEquals(水.toString(), "水")
  }

  @Test
  fun testGetFiveElement() {
    assertEquals(木.fiveElement, 木)
    assertEquals(火.fiveElement, 火)
    assertEquals(土.fiveElement, 土)
    assertEquals(金.fiveElement, 金)
    assertEquals(水.fiveElement, 水)
  }

  @Test
  fun testSame() {
    assertSame(木.fiveElement, 木)
    assertSame(火.fiveElement, 火)
    assertSame(土.fiveElement, 土)
    assertSame(金.fiveElement, 金)
    assertSame(水.fiveElement, 水)
  }

  @Test
  fun testGetProduct() {
    assertSame(木.product, 火)
    assertSame(火.product, 土)
    assertSame(土.product, 金)
    assertSame(金.product, 水)
    assertSame(水.product, 木)
  }

  @Test
  fun testIsProducingTo() {
    assertTrue(木.isProducingTo(火))
    assertTrue(火.isProducingTo(土))
    assertTrue(土.isProducingTo(金))
    assertTrue(金.isProducingTo(水))
    assertTrue(水.isProducingTo(木))
  }

  @Test
  fun testGetProducer() {
    assertSame(木.producer, 水)
    assertSame(火.producer, 木)
    assertSame(土.producer, 火)
    assertSame(金.producer, 土)
    assertSame(水.producer, 金)
  }

  @Test
  fun testIsProducedBy() {
    assertTrue(木.isProducedBy(水))
    assertTrue(火.isProducedBy(木))
    assertTrue(土.isProducedBy(火))
    assertTrue(金.isProducedBy(土))
    assertTrue(水.isProducedBy(金))
  }

  @Test
  fun testGetDominateOver() {
    assertSame(木.dominateOver, 土)
    assertSame(火.dominateOver, 金)
    assertSame(土.dominateOver, 水)
    assertSame(金.dominateOver, 木)
    assertSame(水.dominateOver, 火)
  }

  @Test
  fun testIsDominatorOf() {
    assertTrue(木.isDominatorOf(土))
    assertTrue(火.isDominatorOf(金))
    assertTrue(土.isDominatorOf(水))
    assertTrue(金.isDominatorOf(木))
    assertTrue(水.isDominatorOf(火))
  }

  @Test
  fun testGetDominator() {
    assertSame(木.dominator, 金)
    assertSame(火.dominator, 水)
    assertSame(土.dominator, 木)
    assertSame(金.dominator, 火)
    assertSame(水.dominator, 土)
  }

  @Test
  fun testIsBeatenBy() {
    assertTrue(木.isDominatedBy(金))
    assertTrue(火.isDominatedBy(水))
    assertTrue(土.isDominatedBy(木))
    assertTrue(金.isDominatedBy(火))
    assertTrue(水.isDominatedBy(土))
  }

}

