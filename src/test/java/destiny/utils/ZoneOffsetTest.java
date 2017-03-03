/**
 * Created by smallufo on 2017-03-04.
 */
package destiny.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class ZoneOffsetTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testOf_8H() {
    ZoneOffset zf = ZoneOffset.of("+8");
    logger.info("zf = {} , id = {}", zf, zf.getId());
  }

  /**
   * 8小時零1分
   */
  @Test
  public void testOf_8H1m() {
    ZoneOffset zf = ZoneOffset.of("+08:01");
    logger.info("zf = {} , id = {}", zf, zf.getId());
  }

  /**
   * Asia/Taipei 於 1975 夏天，有實施 日光節約時間 , zf 應該為 "+09:00"
   */
  @Test
  public void testAsiaTaipei() {
    LocalDateTime lmt = LocalDateTime.of(1976, 6, 1, 0, 0);
    ZoneOffset zf = lmt.atZone(ZoneId.of("Asia/Taipei")).getOffset();
    logger.debug("zf = {} , id = {} , 秒數 = {}", zf, zf.getId() , zf.getTotalSeconds());
    assertEquals("+08:00" , zf.getId());
    assertEquals(8*60*60 , zf.getTotalSeconds());

    lmt = LocalDateTime.of(1975, 6, 1, 0, 0);
    zf = lmt.atZone(ZoneId.of("Asia/Taipei")).getOffset();
    logger.debug("zf = {} , id = {} , 秒數 = {}", zf, zf.getId() , zf.getTotalSeconds());
    assertEquals("+09:00" , zf.getId());
    assertEquals(9*60*60 , zf.getTotalSeconds());
  }
}
