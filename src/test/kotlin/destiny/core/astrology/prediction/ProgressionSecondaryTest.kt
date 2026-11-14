/**
 * Created by smallufo on 2022-07-26.
 */
package destiny.core.astrology.prediction

import destiny.core.calendar.*
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.tools.KotlinLogging
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ProgressionSecondaryTest {

  val loc = locationOf(Locale.TAIWAN)

  val julDayResolver = JulDayResolver1582CutoverImpl()



  val logger = KotlinLogging.logger { }

  @Test
  fun test2022_normal() {
    val progression = ProgressionSecondary()

    val natalLmt = LocalDateTime.of(2000, 1, 1, 0, 0)
    val natalGmtJulDay = natalLmt.toGmtJulDay(loc)

    val now = LocalDateTime.of(2022, 7, 26, 0, 30)
    val nowGmtJulDay = now.toGmtJulDay(loc)

    progression.getConvergentTime(natalGmtJulDay, nowGmtJulDay).also { convergentJulDay: GmtJulDay ->

      logger.info { "convergentJulDay = $convergentJulDay" }
      val convergentLmt = TimeTools.getLmtFromGmt(convergentJulDay, loc, julDayResolver)
      logger.info { "convergentLmt = $convergentLmt" }
      assertEquals(natalLmt.toLocalDate().plusDays(22), convergentLmt.toLocalDate())

      progression.getDivergentTime(natalGmtJulDay, convergentJulDay).also { divergentJulDay: GmtJulDay ->
        logger.info { "divergentJulDay = $divergentJulDay" }
        val divergentLmt = TimeTools.getLmtFromGmt(divergentJulDay, loc, julDayResolver)
        logger.info { "divergentLmt = $divergentLmt" }

        assertEquals(0, Duration.between(now, divergentLmt).abs().seconds)
      }

      progression.getDivergentTime(natalGmtJulDay, convergentJulDay).also { divergentGmtJulDay ->
        logger.info { "divergentLmt = ${divergentGmtJulDay.toLmt(loc, julDayResolver)}" }
        assertEquals(0 , nowGmtJulDay.absDuration(divergentGmtJulDay).inWholeSeconds)
      }
    }
  }

  @Test
  fun test2022_converse() {

    val progression = ProgressionSecondary(false)

    val natalLmt = LocalDateTime.of(2000, 1, 1, 0, 0)
    val natalGmtJulDay = natalLmt.toGmtJulDay(loc)

    val now = LocalDateTime.of(2022, 7, 26, 0, 30)
    val nowGmtJulDay = now.toGmtJulDay(loc)

    progression.getConvergentTime(natalGmtJulDay, nowGmtJulDay).also { convergentJulDay: GmtJulDay ->
      logger.info { "convergentJulDay = $convergentJulDay" }
      val convergentLmt = TimeTools.getLmtFromGmt(convergentJulDay, loc, julDayResolver)
      logger.info { "convergentLmt = $convergentLmt" }
      assertEquals(natalLmt.toLocalDate().minusDays(23), convergentLmt.toLocalDate())

      progression.getDivergentTime(natalGmtJulDay, convergentJulDay).also { divergentJulDay: GmtJulDay ->
        logger.info { "divergentJulDay = $divergentJulDay" }
        val divergentLmt = TimeTools.getLmtFromGmt(divergentJulDay, loc, julDayResolver)
        logger.info { "divergentLmt = $divergentLmt" }

        assertEquals(0, Duration.between(now, divergentLmt).abs().seconds)
      }

      progression.getDivergentTime(natalGmtJulDay, convergentJulDay).also { divergentGmtJulDay ->
        logger.info { "divergentLmt = ${divergentGmtJulDay.toLmt(loc, julDayResolver)}" }
        assertEquals(0 , nowGmtJulDay.absDuration(divergentGmtJulDay).inWholeSeconds)
      }
    }
  }

  /** nowTime 早於出生：收斂時間也在出生之前，區間不會倒過來，且仍是 [getDivergentTime] 的反函數 */
  @Test
  fun beforeBirth_monotonic() {
    val natalGmtJulDay = LocalDateTime.of(1976, 5, 15, 11, 30).toGmtJulDay(loc)
    val from = LocalDateTime.of(50, 1, 1, 0, 0).toGmtJulDay(loc)
    val to = LocalDateTime.of(51, 1, 1, 0, 0).toGmtJulDay(loc)

    listOf(ProgressionSecondary(), ProgressionSecondary(false), ProgressionTertiary(), ProgressionMinor()).forEach { progression ->
      val convFrom = progression.getConvergentTime(natalGmtJulDay, from)
      val convTo = progression.getConvergentTime(natalGmtJulDay, to)
      if (progression.forward) {
        assertTrue(convFrom < convTo, "${progression.type} : $convFrom should < $convTo")
        assertTrue(convTo < natalGmtJulDay)
      } else {
        assertTrue(convFrom > convTo, "${progression.type} converse : $convFrom should > $convTo")
      }
      assertEquals(0, from.absDuration(progression.getDivergentTime(natalGmtJulDay, convFrom)).inWholeSeconds)
    }
  }
}
