/**
 * @author smallufo 
 * Created on 2006/6/12 at 上午 02:48:41
 */ 
package destiny.core.calendar.eightwords;

import junit.framework.TestCase;
import destiny.core.chinese.StemBranch;

public class EightWordsNullableTest extends TestCase
{
  /** 把空的干支傳入 */
  public void testHashCode()
  {
    EightWordsNullable eightWords = new EightWordsNullable(null,StemBranch.get("乙丑"),StemBranch.get("丙寅"),StemBranch.get("丁卯"));
    assertTrue(eightWords.hashCode() != 0);
    eightWords = new EightWordsNullable(StemBranch.get("甲子"),null,StemBranch.get("丙寅"),StemBranch.get("丁卯"));
    assertTrue(eightWords.hashCode() != 0);
    eightWords = new EightWordsNullable(StemBranch.get("甲子"),StemBranch.get("乙丑"),null,StemBranch.get("丁卯"));
    assertTrue(eightWords.hashCode() != 0);
    eightWords = new EightWordsNullable(StemBranch.get("甲子"),StemBranch.get("乙丑"),StemBranch.get("丙寅"),null);
    assertTrue(eightWords.hashCode() != 0);
    eightWords = new EightWordsNullable();
    assertTrue(eightWords.hashCode() != 0);
  }
  
  /** 兩個都是 null 的八字，應該 equals */
  public void testEquals()
  {
    EightWordsNullable eightWords = new EightWordsNullable();
    EightWordsNullable eightWords2 = new EightWordsNullable(); 
    assertEquals(eightWords , eightWords2); //兩個都是 null 八字,應該 equals    
  }
  
  public void testNullStemBranch()
  {
    EightWordsNullable eightWords1 = new EightWordsNullable();
    //eightWords1.setYear ("甲子");
    eightWords1.setMonth("乙丑");
    eightWords1.setDay  ("丙寅");
    eightWords1.setHour ("丁卯");
    assertNotNull(eightWords1);
    assertNotNull(eightWords1.toString());
    assertTrue(eightWords1.hashCode() != 0);
    
    EightWordsNullable eightWords2 = new EightWordsNullable();
    eightWords2.setYear ("甲子");
    //eightWords2.setMonth("乙丑");
    eightWords2.setDay  ("丙寅");
    eightWords2.setHour ("丁卯");
    assertNotNull(eightWords2);
    assertNotNull(eightWords2.toString());
    assertTrue(eightWords2.hashCode() != 0);
    
    EightWordsNullable eightWords3 = new EightWordsNullable();
    eightWords3.setYear ("甲子");
    eightWords3.setMonth("乙丑");
    //eightWords3.setDay  ("丙寅");
    eightWords3.setHour ("丁卯");
    assertNotNull(eightWords3);
    assertNotNull(eightWords3.toString());
    assertTrue(eightWords3.hashCode() != 0);
    
    EightWordsNullable eightWords4 = new EightWordsNullable();
    eightWords4.setYear ("甲子");
    eightWords4.setMonth("乙丑");
    eightWords4.setDay  ("丙寅");
    //eightWords4.setHour ("丁卯");
    assertNotNull(eightWords4);
    assertNotNull(eightWords4.toString());
    assertTrue(eightWords4.hashCode() != 0);

  }
}
