/**
 * Created by smallufo on 2017-09-29.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;

import static org.junit.Assert.assertEquals;

public class ZoneOffsetTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testZone() {
    ZoneOffset offset = ZoneOffset.of("+8");
    logger.info("offset = {}" , offset);
  }

  @Test
  public void sqlTimestamp() {
    LocalDateTime epochLMT = LocalDateTime.of(1970 , 1 , 1 , 0,0);
    ZonedDateTime epochZonedGMT = ZonedDateTime.of(epochLMT , ZoneId.of("GMT"));
    ChronoLocalDateTime cldt = LocalDateTime.of(1970 , 1 , 1 , 0,0);
    ZoneId sysDefaultZoneId = ZoneId.systemDefault();
    ZoneId GMT = ZoneId.of("GMT");


    logger.info("sysDefaultZoneId = {}" , sysDefaultZoneId);
    logger.info("cldt = {}" , cldt);
    logger.info("epochLMT = {}" , epochLMT);
    logger.info("epochLMT.toInstant(UTC) = {}" , epochLMT.toInstant(ZoneOffset.UTC));
    logger.info("epochLMT.toInstant(ZoneOffset(8HR)) = {}" , epochLMT.toInstant(ZoneOffset.ofHours(8)));

    ZoneOffset offset = sysDefaultZoneId.getRules().getOffset(epochLMT);
    logger.info("offset = {}" , offset);



    Timestamp epochLmtTS = Timestamp.valueOf(epochLMT);
    logger.info("舊有演算法的 epochLmtTS = {} , getTime = {}" , epochLmtTS , epochLmtTS.getTime());
    // 這裡會抓系統的 ZonedOffset (GMT+8) , 所以，當台灣已經到 1970-01-01 00:00 時， GMT 還有八小時，所以會傳回 -8小時的 millis 之值
    assertEquals(-8*60*60*1000 , epochLmtTS.getTime());

    Timestamp epochGmtTS = Timestamp.from(epochZonedGMT.toInstant());
    logger.info("epochGmtTS = {} , getTime = {}" , epochGmtTS , epochGmtTS.getTime());
    assertEquals(0 , epochGmtTS.getTime()); // GMT 定義，就是傳回 0

    Timestamp newTS = Timestamp.from(cldt.atZone(sysDefaultZoneId).toInstant());
    logger.info("新演算法 : {} , getTime = {}" , newTS , newTS.getTime());

  }
}
