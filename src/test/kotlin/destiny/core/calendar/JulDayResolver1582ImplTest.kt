/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import destiny.core.calendar.Constants.CutOver1582
import destiny.core.calendar.Constants.JulianYear1
import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.core.calendar.Constants.UnixEpoch.JULIAN_SECONDS
import kotlinx.datetime.Instant
import mu.KotlinLogging
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JulDayResolver1582ImplTest {

  val impl = JulDayResolver1582Impl()

  val logger = KotlinLogging.logger { }

  private fun testData() = Stream.of(
    CutOver1582.FROM_UNIXEPOCH_SECONDS to "1582-10-15T00:00",
    (CutOver1582.FROM_UNIXEPOCH_SECONDS - SECONDS_OF_DAY) to "1582-10-04T00:00",
    0 to "1970-01-01T00:00",
    JulianYear1.FROM_UNIXEPOCH_DAYS * SECONDS_OF_DAY to "0001-01-01T00:00",     // 西元元年一月一日
    (JulianYear1.FROM_UNIXEPOCH_DAYS - 1) * SECONDS_OF_DAY to "0000-12-31T00:00", // 西元前一年12月31日
    (JulianYear1.FROM_UNIXEPOCH_DAYS - 366) * SECONDS_OF_DAY to "0000-01-01T00:00", // 西元前一年1月1日
    (JulianYear1.FROM_UNIXEPOCH_DAYS - 367) * SECONDS_OF_DAY to "-0001-12-31T00:00", // 西元前二年12月31日
    0 - JULIAN_SECONDS to "-4712-01-01T12:00", // 西元前4713年1月1日
  )

  @ParameterizedTest
  @MethodSource("testData")
  fun testInvoke(pair: Pair<Long, String>) {

    val (epochSeconds, ldtString) = pair

    val instant = Instant.fromEpochSeconds(epochSeconds)
    assertEquals(ldtString, impl.invoke(instant).first.toString())
  }
}
