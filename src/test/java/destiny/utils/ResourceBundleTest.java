/**
 * @author smallufo
 * Created on 2011/5/3 at 上午7:49:41
 */
package destiny.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class ResourceBundleTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testResource() {
    ResourceBundle rb = ResourceBundle.getBundle("destiny.utils.testBundle", Locale.CANADA_FRENCH);
    String pattern = rb.getString("hello");
    String result = MessageFormat.format(pattern, "smallufo", "test");
    assertEquals("哈囉 smallufo test" , result);
  }
}
