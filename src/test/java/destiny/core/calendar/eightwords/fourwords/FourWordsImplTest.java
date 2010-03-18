/** 2009/10/12 上午3:25:51 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import junit.framework.TestCase;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.StemBranch;

public class FourWordsImplTest extends TestCase
{

  public void testGetResult()
  {
    FourWordsImpl impl = new FourWordsImpl();
    EightWords ew = new EightWords(StemBranch.get("甲子"),StemBranch.get("乙丑"),StemBranch.get("丙寅"),StemBranch.get("丁卯"));
    assertEquals("推枕被衾" , impl.getResult(ew));
  }

}

