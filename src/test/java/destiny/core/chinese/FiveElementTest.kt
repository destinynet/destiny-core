/**
 * @author smallufo
 * Created on 2005/7/2 at 上午 06:11:30
 */
package destiny.core.chinese

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FiveElementTest {
  @Test
  fun testToString() {
    assertEquals(FiveElement.木.toString(), "木")
    assertEquals(FiveElement.火.toString(), "火")
    assertEquals(FiveElement.土.toString(), "土")
    assertEquals(FiveElement.金.toString(), "金")
    assertEquals(FiveElement.水.toString(), "水")
  }

  @Test
  fun testGetFiveElement() {
    assertEquals(FiveElement.木.fiveElement, FiveElement.木)
    assertEquals(FiveElement.火.fiveElement, FiveElement.火)
    assertEquals(FiveElement.土.fiveElement, FiveElement.土)
    assertEquals(FiveElement.金.fiveElement, FiveElement.金)
    assertEquals(FiveElement.水.fiveElement, FiveElement.水)
  }

  @Test
  fun testSame() {
    assertSame(FiveElement.木.fiveElement, FiveElement.木)
    assertSame(FiveElement.火.fiveElement, FiveElement.火)
    assertSame(FiveElement.土.fiveElement, FiveElement.土)
    assertSame(FiveElement.金.fiveElement, FiveElement.金)
    assertSame(FiveElement.水.fiveElement, FiveElement.水)
  }

  @Test
  fun testGetProduct() {
    assertSame(FiveElement.木.product, FiveElement.火)
    assertSame(FiveElement.火.product, FiveElement.土)
    assertSame(FiveElement.土.product, FiveElement.金)
    assertSame(FiveElement.金.product, FiveElement.水)
    assertSame(FiveElement.水.product, FiveElement.木)
  }

  @Test
  fun testIsProducingTo() {
    assertTrue(FiveElement.木.isProducingTo(FiveElement.火))
    assertTrue(FiveElement.火.isProducingTo(FiveElement.土))
    assertTrue(FiveElement.土.isProducingTo(FiveElement.金))
    assertTrue(FiveElement.金.isProducingTo(FiveElement.水))
    assertTrue(FiveElement.水.isProducingTo(FiveElement.木))
  }

  @Test
  fun testGetProducer() {
    assertSame(FiveElement.木.producer, FiveElement.水)
    assertSame(FiveElement.火.producer, FiveElement.木)
    assertSame(FiveElement.土.producer, FiveElement.火)
    assertSame(FiveElement.金.producer, FiveElement.土)
    assertSame(FiveElement.水.producer, FiveElement.金)
  }

  @Test
  fun testIsProducedBy() {
    assertTrue(FiveElement.木.isProducedBy(FiveElement.水))
    assertTrue(FiveElement.火.isProducedBy(FiveElement.木))
    assertTrue(FiveElement.土.isProducedBy(FiveElement.火))
    assertTrue(FiveElement.金.isProducedBy(FiveElement.土))
    assertTrue(FiveElement.水.isProducedBy(FiveElement.金))
  }

  @Test
  fun testGetDominateOver() {
    assertSame(FiveElement.木.dominateOver, FiveElement.土)
    assertSame(FiveElement.火.dominateOver, FiveElement.金)
    assertSame(FiveElement.土.dominateOver, FiveElement.水)
    assertSame(FiveElement.金.dominateOver, FiveElement.木)
    assertSame(FiveElement.水.dominateOver, FiveElement.火)
  }

  @Test
  fun testIsDominatorOf() {
    assertTrue(FiveElement.木.isDominatorOf(FiveElement.土))
    assertTrue(FiveElement.火.isDominatorOf(FiveElement.金))
    assertTrue(FiveElement.土.isDominatorOf(FiveElement.水))
    assertTrue(FiveElement.金.isDominatorOf(FiveElement.木))
    assertTrue(FiveElement.水.isDominatorOf(FiveElement.火))
  }

  @Test
  fun testGetDominator() {
    assertSame(FiveElement.木.dominator, FiveElement.金)
    assertSame(FiveElement.火.dominator, FiveElement.水)
    assertSame(FiveElement.土.dominator, FiveElement.木)
    assertSame(FiveElement.金.dominator, FiveElement.火)
    assertSame(FiveElement.水.dominator, FiveElement.土)
  }

  @Test
  fun testIsBeatenBy() {
    assertTrue(FiveElement.木.isDominatedBy(FiveElement.金))
    assertTrue(FiveElement.火.isDominatedBy(FiveElement.水))
    assertTrue(FiveElement.土.isDominatedBy(FiveElement.木))
    assertTrue(FiveElement.金.isDominatedBy(FiveElement.火))
    assertTrue(FiveElement.水.isDominatedBy(FiveElement.土))
  }

}

