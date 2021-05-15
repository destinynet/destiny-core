/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.HoroscopeModel
import destiny.core.astrology.IBesieged
import destiny.core.astrology.IRelativeTransit
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.rules.Misc
import mu.KotlinLogging
import java.io.Serializable


interface IVoidCourse {

  fun getVoidCourse(h: HoroscopeModel, planet: Planet = Planet.MOON): Misc.VoidCourse?
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
 */
class VoidCourseHellenistic(private val relativeTransit: IRelativeTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: HoroscopeModel, planet: Planet): Misc.VoidCourse? {
    TODO("Not yet implemented")
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with another planet
 * until it moves into the following sign of the zodiac.
 */
class VoidCourseMedieval(private val besiegedImpl : IBesieged) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: HoroscopeModel, planet: Planet): Misc.VoidCourse? {

    val otherPlanets = Planet.classicalList.filter { it != planet }


    TODO("Not yet implemented")
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
