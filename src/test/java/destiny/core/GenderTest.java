package destiny.core;

import junit.framework.TestCase;

public class GenderTest extends TestCase
{
  public void testToString()
  {
    Gender gender = Gender.男;
    assertEquals(gender.toString() , "男");
    gender = Gender.女;
    assertEquals(gender.toString() , "女");
  }

}
