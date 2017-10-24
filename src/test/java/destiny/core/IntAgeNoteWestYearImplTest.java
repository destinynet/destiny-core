/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class IntAgeNoteWestYearImplTest {

  @Test
  public void getTitle() throws Exception {
    IntAgeNoteWestYearImpl impl = new IntAgeNoteWestYearImpl();
    assertEquals("西元", impl.getTitle(Locale.TAIWAN));
    assertEquals("西元", impl.getTitle(Locale.CHINA));
    assertEquals("Year", impl.getTitle(Locale.ENGLISH));
  }

}