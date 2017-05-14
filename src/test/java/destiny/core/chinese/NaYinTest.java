/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class NaYinTest {

  @Test
  public void testNaYin() {
    for(StemBranch sb : StemBranch.iterable()) {
      assertNotNull(sb);
      assertNotNull(NaYin.getFiveElement(sb));
      assertNotNull(NaYin.getDesc(sb, Locale.TAIWAN));
      System.out.println(sb + " : " + NaYin.getDesc(sb, Locale.TAIWAN) + " , 五行 : " + NaYin.getFiveElement(sb));
    }
  }
}