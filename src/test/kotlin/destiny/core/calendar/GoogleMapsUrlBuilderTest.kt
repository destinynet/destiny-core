/**
 * Created by smallufo on 2017-03-22.
 */
package destiny.core.calendar

import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test

class GoogleMapsUrlBuilderTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun getUrl() {
    val builder = GoogleMapsUrlBuilder()
    val location = locationOf(Locale.TAIWAN)
    val s = builder.getUrl(location.lat, location.lng)
    logger.info("{}", s)

    logger.info("{}", builder.getUrl(locationOf(Locale("zh", "HK"))))
    logger.info("{}", builder.getUrl(locationOf(Locale.US)))
  }

}
