/**
 * @author smallufo 
 * Created on 2006/6/11 at 下午 11:23:55
 */ 
package destiny.core.calendar.eightwords;

import destiny.core.chinese.StemBranch;
import junit.framework.TestCase;

public class EightWordsTest extends TestCase
{
  public void testEqualsSame()
  {
    EightWords expected=new EightWords("甲子","甲子","甲子","甲子");
    EightWords actual = new EightWords(StemBranch.get("甲子"),StemBranch.get("甲子"),StemBranch.get("甲子"),StemBranch.get("甲子"));
    assertEquals(expected , actual);
    assertNotSame(expected , actual); //不應該 same
  }
  
  /** 測試傳入 null 到 EightWords 的 constructor 中 */
  public void testNullInconstructor1()
  {
    try
    {
      new EightWords(null,StemBranch.get("乙丑"),StemBranch.get("丙寅"),StemBranch.get("丁卯"));
      fail("year's StemBranch is null , should throw RuntimeException !");
      new EightWords(StemBranch.get("甲子"),null,StemBranch.get("丙寅"),StemBranch.get("丁卯"));
      fail("month's StemBranch is null , should throw RuntimeException !");
      new EightWords(StemBranch.get("甲子"),StemBranch.get("乙丑"),null,StemBranch.get("丁卯"));
      fail("day's StemBranch is null , should throw RuntimeException !");
      new EightWords(StemBranch.get("甲子"),StemBranch.get("乙丑"),StemBranch.get("丙寅"),null);
      fail("day's StemBranch is null , should throw RuntimeException !");
    }
    catch (RuntimeException expected)
    {
      assertTrue(true);
    }
  }
  
  /** 測試傳入 null 到 EightWords 的 constructor 中 , 與前例不同在於 , 前例傳的是干支 , 本例傳的是字串 */
  public void testNullInconstructor2()
  {
    try
    {
      new EightWords(null,"乙丑","丙寅","丁卯");
      fail("year's StemBranch is null , should throw RuntimeException !");
      new EightWords("甲子",null,"丙寅","丁卯");
      fail("month's StemBranch is null , should throw RuntimeException !");
      new EightWords("甲子","乙丑",null,"丁卯");
      fail("day's StemBranch is null , should throw RuntimeException !");
      new EightWords("甲子","乙丑","丙寅",null);
      fail("hour's StemBranch is null , should throw RuntimeException !");
    }
    catch (RuntimeException expected)
    {
      assertTrue(true);
    }
  }
  
  public void testNullStemBranch()
  {
    EightWords eightWordsFull = new EightWords(StemBranch.get("甲子"),StemBranch.get("乙丑"),StemBranch.get("丙寅"),StemBranch.get("丁卯"));
    assertNotNull(eightWordsFull);
    assertNotNull(eightWordsFull.toString());
    System.out.println(eightWordsFull.toString());
  }
}
