/**
 * Created by smallufo on 2022-07-26.
 */
package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ProgressionTertiaryTest {

  val loc = locationOf(Locale.TAIWAN)

  val julDayResolver = JulDayResolver1582CutoverImpl()

  private val progression = ProgressionTertiary()

  val logger = KotlinLogging.logger { }

  @Test
  fun test2022() {
    val natalLmt = LocalDateTime.of(2000, 1, 1, 0, 0)
    val natalGmtJulDay = natalLmt.toGmtJulDay(loc)

    val now = LocalDateTime.of(2022, 7, 26, 0, 30)
    val nowGmtJulDay = now.toGmtJulDay(loc)

    progression.getConvergentTime(natalGmtJulDay, nowGmtJulDay).also { convergentJulDay: GmtJulDay ->
      logger.info { "convergentJulDay = $convergentJulDay" }
      val convergentLmt = TimeTools.getLmtFromGmt(convergentJulDay, loc, julDayResolver)
      logger.info { "convergentLmt = $convergentLmt" }
      // 大約 278 個月
      assertEquals(natalLmt.toLocalDate().plusDays(278), convergentLmt.toLocalDate())

      progression.getDivergentTime(natalGmtJulDay, convergentJulDay).also { divergentJulDay: GmtJulDay ->
        logger.info { "divergentJulDay = $divergentJulDay" }
        val divergentLmt = TimeTools.getLmtFromGmt(divergentJulDay, loc, julDayResolver)
        logger.info { "divergentLmt = $divergentLmt" }

        assertEquals(0, Duration.between(now, divergentLmt).abs().seconds)
      }
    }
  }
}
