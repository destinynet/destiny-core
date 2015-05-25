package destiny.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenderTest
{
  @Test
  public void testToString()
  {
    Gender gender = Gender.男;
    assertEquals(gender.toString() , "男");
    gender = Gender.女;
    assertEquals(gender.toString() , "女");
  }

}
