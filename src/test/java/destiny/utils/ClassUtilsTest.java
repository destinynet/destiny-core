/**
 * @author smallufo
 * Created on 2010/12/15 at 下午4:08:15
 */
package destiny.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import destiny.utils.ClassUtils.PERMISSION;
import destiny.utils.ColorCanvas.ColorCanvas;

public class ClassUtilsTest
{
  @Test
  public void testGetProperties() throws Exception
  {
    for(String s : ClassUtils.getProperties(ColorCanvas.class , PERMISSION.READABLE))
    {
      System.out.println(s);
    }
  }

  @Test
  public void testIsWritable() throws Exception
  {
    assertTrue(!ClassUtils.isWritable(ColorCanvas.class , "width"));
  }
}
