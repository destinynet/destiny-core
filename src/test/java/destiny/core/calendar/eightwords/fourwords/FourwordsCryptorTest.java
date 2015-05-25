/** 2009/10/21 上午12:05:46 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FourwordsCryptorTest
{

  @Test
  public void testGetEncodedString()
  {
    FourwordsCryptor c = new FourwordsCryptor("fourwords");
    assertEquals("PDrOZ9oIGtw" , c.getEncodedString("1"));
    assertEquals("A4XspjXk9NQ" , c.getEncodedString("2"));
    assertEquals("v0vLe_x7oMg" , c.getEncodedString("3"));
  }

  @Test
  public void testGetDecodedString()
  {
    FourwordsCryptor c = new FourwordsCryptor("fourwords");
    assertEquals("1428" , c.getDecodedString("gZisj1WXBTU="));
  }

}

