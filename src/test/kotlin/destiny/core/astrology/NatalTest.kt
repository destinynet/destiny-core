/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.core.astrology

import destiny.core.Circular
import destiny.core.Gender
import destiny.core.Graph
import destiny.core.astrology.AstroPattern.GrandTrine
import destiny.core.astrology.AstroPattern.Wedge
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign.*
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.TimeTools.toGmtLocaleDateTime
import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class NatalTest {

  private val logger = KotlinLogging.logger {}

  private val json = Json {
    prettyPrint = true
  }


  @Test
  fun testSerialize() {
    val ldt = LocalDateTime.of(2000, 1, 1, 12, 0)
    val loc = locationOf(Locale.TAIWAN)
    val utc = ldt.toGmtLocaleDateTime(loc.zoneId) as LocalDateTime

    val horoscopeDto = HoroscopeDto(
      ldt, utc, loc, "Taipei", mapOf(
        ARIES to listOf(SUN, MOON),
        LEO to listOf(MARS)
      ),
      listOf(
        HouseDto(1, ZodiacDegree.of(ARIES, 20.0), MOON, listOf(SUN, MOON)),
        HouseDto(2, ZodiacDegree.of(LEO, 12.0), JUPITER, listOf(MARS))
      ),
      mapOf(
        SUN to Natal.StarPosInfo(
          ZodiacDegree.of(ARIES, 10.0),
          Element.WATER,
          Quality.CARDINAL,
          1,
          Motion.DIRECT,
          null,
          setOf(
            RulingHouse(1, LEO),
            RulingHouse(2, ARIES)
          ),
          setOf(MARS),
          listOf(
            AspectData.of(SUN, JUPITER, Aspect.TRINE, 0.8, 0.99.toScore(), IPointAspectPattern.AspectType.APPLYING, ldt.toGmtJulDay(loc))
          )
        ),
        MERCURY to Natal.StarPosInfo(
          ZodiacDegree.of(LEO, 10.0),
          Element.FIRE,
          Quality.FIXED,
          2,
          Motion.RETROGRADE,
          RetrogradePhase.RETROGRADING,
          setOf(
            RulingHouse(2, SCORPIO),
          ),
          setOf(VENUS),
          listOf(
            AspectData.of(MERCURY, SATURN, Aspect.SEXTILE, 2.5, 0.85.toScore(), IPointAspectPattern.AspectType.SEPARATING, ldt.toGmtJulDay(loc))
          )
        )
      ),
      mapOf(
        Axis.RISING to listOf(AxisStar(SUN, 0.5, 0.99.toScore())),
        Axis.MERIDIAN to listOf(
          AxisStar(MOON, 1.0, 0.92.toScore()),
          AxisStar(JUPITER, 0.5, 0.95.toScore()),
        ),
      ),
      mapOf(
        HouseType.ANGULAR to Natal.HouseStarDistribution(5, 0.5),
        HouseType.SUCCEDENT to Natal.HouseStarDistribution(3, 0.3),
        HouseType.CADENT to Natal.HouseStarDistribution(2, 0.2),
      ),
      mapOf(
        Element.EARTH to 0.1667,
        Element.WATER to 0.3333,
        Element.FIRE to 0.3333,
        Element.AIR to .01667,
      ),
      mapOf(
        Quality.FIXED to 0.25,
        Quality.CARDINAL to 0.4166,
        Quality.MUTABLE to 0.3333
      ),
      listOf(
        AspectData.of(SUN, JUPITER, Aspect.TRINE, 0.8, 0.99.toScore(), IPointAspectPattern.AspectType.APPLYING, ldt.toGmtJulDay(loc)),
        AspectData.of(MOON, VENUS, Aspect.SEXTILE, 0.2, 0.98.toScore(), IPointAspectPattern.AspectType.SEPARATING, ldt.toGmtJulDay(loc)),
      ),
      listOf(
        GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.91.toScore()),
        Wedge(setOf(MARS, SATURN), PointSignHouse(JUPITER, LEO, 1), 0.95.toScore())
      ),
      listOf(
        "pending"
      ),
      Graph(
        setOf(
          Circular(listOf(MOON, SATURN))
        ),
        setOf(
          listOf(MARS, SUN, VENUS),
          listOf(JUPITER, VENUS)
        ),
        setOf(), setOf()
      ),
      listOf(
        MidPointWithFocal(MidPoint(setOf(VENUS, MERCURY), ZodiacDegree.of(ARIES, 10.0), 2), JUPITER, 0.23)
      )
    )

    val natal = Natal(Gender.男, 10 , "Kevin" , horoscopeDto)

    json.encodeToString(natal).also { rawJson ->
      logger.info { rawJson }
      //json.decodeFromString<Natal>(rawJson).also { parsed -> assertEquals(natal, parsed) }
    }
  }
}
