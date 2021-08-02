/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import destiny.core.calendar.IJulDayResolver.Companion.GREGORIAN_START_EPOCH_SECONDS
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

  private fun testInvoke() = Stream.of(
    GREGORIAN_START_EPOCH_SECONDS to "1582-10-15T00:00",
    0 to "1970-01-01T00:00"
  )

  @ParameterizedTest
  @MethodSource("testInvoke")
  fun testInvoke(pair: Pair<Long, String>) {

    val (epochSeconds, ldtString) = pair

    val instant = Instant.fromEpochSeconds(epochSeconds)
    assertEquals(ldtString, impl.invoke(instant).toString())
  }
}
