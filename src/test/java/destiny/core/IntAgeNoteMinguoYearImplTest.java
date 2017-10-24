/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class IntAgeNoteMinguoYearImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void getTitle() throws Exception {
    IntAgeNoteMinguoYearImpl impl = new IntAgeNoteMinguoYearImpl();
    assertEquals("民國", impl.getTitle(Locale.TAIWAN));
    assertEquals("民国", impl.getTitle(Locale.CHINA));
    assertEquals("Minguo", impl.getTitle(Locale.ENGLISH));
  }

}