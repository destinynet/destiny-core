/**
 * Created by smallufo on 2017-03-22.
 */
package destiny.core.calendar

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test

class GoogleMapsUrlBuilderTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun getUrl() {
    val builder = GoogleMapsUrlBuilder()
    val location = Location.of(Locale.TAIWAN)
    val s = builder.getUrl(location.lat, location.lng)
    logger.info("{}", s)

    logger.info("{}", builder.getUrl(Location.of(Locale("zh" , "HK"))))
    logger.info("{}", builder.getUrl(Location.of(Locale.US)))
  }

}