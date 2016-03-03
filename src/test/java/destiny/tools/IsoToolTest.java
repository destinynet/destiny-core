/**
 * Created by smallufo on 2016-03-01.
 */
package destiny.tools;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsoToolTest {

  @Test
  public void testIsValidLanguage() throws Exception {
    assertTrue(IsoTool.isValidCountry("TW"));
    assertTrue(IsoTool.isValidCountry("tw"));
    assertFalse(IsoTool.isValidCountry("TWN"));
  }

  @Test
  public void testIsValidCountry() throws Exception {
    assertTrue(IsoTool.isValidLanguage("zh"));
    assertTrue(IsoTool.isValidLanguage("ZH"));
  }
}