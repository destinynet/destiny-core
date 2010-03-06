/**
 * @author smallufo 
 * Created on 2005/7/2 at 上午 06:11:30
 */ 
package destiny.core.chinese;

import junit.framework.TestCase;

public class FiveElementTest extends TestCase
{
  public void testToString()
  {
    assertEquals(FiveElement.木.toString() , "木");
    assertEquals(FiveElement.火.toString() , "火");
    assertEquals(FiveElement.土.toString() , "土");
    assertEquals(FiveElement.金.toString() , "金");
    assertEquals(FiveElement.水.toString() , "水");
  }

  public void testGetFiveElement()
  {
    assertEquals(FiveElement.木.getFiveElement() , FiveElement.木);
    assertEquals(FiveElement.火.getFiveElement() , FiveElement.火);
    assertEquals(FiveElement.土.getFiveElement() , FiveElement.土);
    assertEquals(FiveElement.金.getFiveElement() , FiveElement.金);
    assertEquals(FiveElement.水.getFiveElement() , FiveElement.水);
  }

  public void testSame()
  {
    assertSame(FiveElement.木.getFiveElement() , FiveElement.木);
    assertSame(FiveElement.火.getFiveElement() , FiveElement.火);
    assertSame(FiveElement.土.getFiveElement() , FiveElement.土);
    assertSame(FiveElement.金.getFiveElement() , FiveElement.金);
    assertSame(FiveElement.水.getFiveElement() , FiveElement.水);

  }

  public void testGetProduct()
  {
    assertSame(FiveElement.木.getProduct() , FiveElement.火);
    assertSame(FiveElement.火.getProduct() , FiveElement.土);
    assertSame(FiveElement.土.getProduct() , FiveElement.金);
    assertSame(FiveElement.金.getProduct() , FiveElement.水);
    assertSame(FiveElement.水.getProduct() , FiveElement.木);
  }

  public void testIsProducingTo()
  {
    assertTrue(FiveElement.木.isProducingTo(FiveElement.火));
    assertTrue(FiveElement.火.isProducingTo(FiveElement.土));
    assertTrue(FiveElement.土.isProducingTo(FiveElement.金));
    assertTrue(FiveElement.金.isProducingTo(FiveElement.水));
    assertTrue(FiveElement.水.isProducingTo(FiveElement.木));
  }

  public void testGetProducer()
  {
    assertSame(FiveElement.木.getProducer() , FiveElement.水);
    assertSame(FiveElement.火.getProducer() , FiveElement.木);
    assertSame(FiveElement.土.getProducer() , FiveElement.火);
    assertSame(FiveElement.金.getProducer() , FiveElement.土);
    assertSame(FiveElement.水.getProducer() , FiveElement.金);
  }

  public void testIsProducedBy()
  {
    assertTrue(FiveElement.木.isProducedBy(FiveElement.水));
    assertTrue(FiveElement.火.isProducedBy(FiveElement.木));
    assertTrue(FiveElement.土.isProducedBy(FiveElement.火));
    assertTrue(FiveElement.金.isProducedBy(FiveElement.土));
    assertTrue(FiveElement.水.isProducedBy(FiveElement.金));
  }

  public void testGetDominateOver()
  {
    assertSame(FiveElement.木.getDominateOver() , FiveElement.土);
    assertSame(FiveElement.火.getDominateOver() , FiveElement.金);
    assertSame(FiveElement.土.getDominateOver() , FiveElement.水);
    assertSame(FiveElement.金.getDominateOver() , FiveElement.木);
    assertSame(FiveElement.水.getDominateOver() , FiveElement.火);
  }

  public void testIsDominatorOf()
  {
    assertTrue(FiveElement.木.isDominatorOf(FiveElement.土));
    assertTrue(FiveElement.火.isDominatorOf(FiveElement.金));
    assertTrue(FiveElement.土.isDominatorOf(FiveElement.水));
    assertTrue(FiveElement.金.isDominatorOf(FiveElement.木));
    assertTrue(FiveElement.水.isDominatorOf(FiveElement.火));
  }

  public void testGetDominator()
  {
    assertSame(FiveElement.木.getDominator() , FiveElement.金);
    assertSame(FiveElement.火.getDominator() , FiveElement.水);
    assertSame(FiveElement.土.getDominator() , FiveElement.木);
    assertSame(FiveElement.金.getDominator() , FiveElement.火);
    assertSame(FiveElement.水.getDominator() , FiveElement.土);
  }

  public void testIsBeatenBy()
  {
    assertTrue(FiveElement.木.isDominatedBy(FiveElement.金));
    assertTrue(FiveElement.火.isDominatedBy(FiveElement.水));
    assertTrue(FiveElement.土.isDominatedBy(FiveElement.木));
    assertTrue(FiveElement.金.isDominatedBy(FiveElement.火));
    assertTrue(FiveElement.水.isDominatedBy(FiveElement.土));
  }

}

