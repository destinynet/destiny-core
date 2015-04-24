/**
 * @author smallufo
 * Created on 2010/12/15 at 下午4:08:15
 */
package destiny.utils;

import destiny.utils.ClassUtils.PERMISSION;
import destiny.utils.ColorCanvas.ColorCanvas;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassUtilsTest
{
  @Test
  public void testGetProperties() throws Exception
  {
    ClassUtils.getProperties(ColorCanvas.class, PERMISSION.READABLE).forEach(System.out::println);
  }

  @Test
  public void testIsWritable() throws Exception
  {
    assertTrue(!ClassUtils.isWritable(ColorCanvas.class , "w"));
  }
}
