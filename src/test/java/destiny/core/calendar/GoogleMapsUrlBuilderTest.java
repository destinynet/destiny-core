/**
 * Created by smallufo on 2017-03-22.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class GoogleMapsUrlBuilderTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void getUrl() throws Exception {
    GoogleMapsUrlBuilder builder = new GoogleMapsUrlBuilder();
    Location location = new Location(Locale.TAIWAN);
    String s = builder.getUrl(location.getLatitude() , location.getLongitude());
    logger.info("{}" , s);

    logger.info("{}" , builder.getUrl(new Location(Locale.US)));
  }

}