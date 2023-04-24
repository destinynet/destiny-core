/**
 * Created by smallufo on 2023-04-24.
 */
package destiny.core.astrology

import destiny.core.astrology.HoroscopeFunctions.getAxisScore
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import io.mockk.every
import io.mockk.mockk
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class HoroscopeFunctionsTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testEvenlyDistributedScore() {
    val model: IHoroscopeModel = mockk<IHoroscopeModel>()

    (1..12).forEach { houseIndex ->
      every { model.getCuspDegree(houseIndex) }.returns(((houseIndex - 1) * 30.0).toZodiacDegree())
    }

    val planet = Planet.SUN

    val azimuth = Azimuth(0.0, 0.0, 0.0)
    
    
    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(0.0, 0.0), azimuth))
      assertEquals(1.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(45.0, 0.0), azimuth))
      assertEquals(0.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(90.0, 0.0), azimuth))
      assertEquals(1.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(135.0, 0.0), azimuth))
      assertEquals(0.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(180.0, 0.0), azimuth))
      assertEquals(1.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(225.0, 0.0), azimuth))
      assertEquals(0.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(270.0, 0.0), azimuth))
      assertEquals(1.0, model.getAxisScore(planet))
    }

    run {
      every { model.getPosition(planet) }.returns(PosWithAzimuth(Pos(315.0, 0.0), azimuth))
      assertEquals(0.0, model.getAxisScore(planet))
    }

  }
}
