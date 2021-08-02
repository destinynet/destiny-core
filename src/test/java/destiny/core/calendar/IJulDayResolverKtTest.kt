/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import mu.KotlinLogging
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IJulDayResolverKtTest {

  val logger = KotlinLogging.logger { }

  private fun julDayToInstant() = Stream.of(
    2440587.5 to "1970-01-01T00:00:00Z",
    2433282.5 to "1950-01-01T00:00:00Z",
    2400000.0 to "1858-11-16T12:00:00Z",
    2299160.5 to "1582-10-15T00:00:00Z",
    2299159.5 to "1582-10-14T00:00:00Z", // proleptic gregorian calendar
    1721425.5 to "0001-01-01T00:00:00Z", // proleptic gregorian calendar
    0.0 to "-4713-11-24T12:00:00Z", // proleptic gregorian calendar , 實際對應到 JD = -4712-01-01T12:00:00Z
  )

  @ParameterizedTest
  @MethodSource("julDayToInstant")
  fun julDayToInstant(pair: Pair<Double, String>) {
    val (jd, instantString) = pair
    assertEquals(instantString, julDayToInstant(jd).toString())
  }
}
