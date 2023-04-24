/**
 * Created by smallufo on 2023-04-23.
 */
package destiny.core.astrology

import mu.KotlinLogging

object HoroscopeFunctions {

  val logger = KotlinLogging.logger { }

  private fun Axis.toHouse() : Int {
    return when(this) {
      Axis.RISING -> 1
      Axis.NADIR -> 4
      Axis.SETTING -> 7
      Axis.MERIDIAN -> 10
    }
  }

  fun IHoroscopeModel.getAxisScore(planet: Planet, axes: Set<Axis> = Axis.list.toSet(), rotatingDeg: Double = 0.0): Double? {

    return this.getPosition(planet)?.lngDeg?.let { planetDeg ->

      val (axis, from, to) = axes.map { axis ->
        axis to when (axis) {
          Axis.RISING   -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

          Axis.NADIR    -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6)) / 2.0)

          Axis.SETTING  -> this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6))) / 2.0 to
            this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9)) / 2.0)

          Axis.MERIDIAN -> this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

          else          -> throw IllegalArgumentException("Invalid house $axis for axis")
        }.let { (from, to) -> from.minus(rotatingDeg) to to.minus(rotatingDeg) }
      }
        .map { (house, fromTo) ->
          val (from, to) = fromTo

          val between = planetDeg.between(from, to)
          Triple(house, between, fromTo)
        }.first { (house, between, fromTo) -> between }
        .let { (house, between, fromTo) ->
          Triple(house, fromTo.first, fromTo.second)
        }

      val axisDeg: ZodiacDegree = this.getCuspDegree(axis.toHouse()).minus(rotatingDeg)
      logger.trace { "$axis deg = ${axisDeg.value} , from ${from.value} to ${to.value}" }
      val axisAngle: Double = axisDeg.getAngle(planetDeg)

      if (planetDeg.between(axisDeg, from)) {
        1 - axisAngle / axisDeg.getAngle(from)
      } else {
        1 - axisAngle / axisDeg.getAngle(to)
      }
    }
  }

}

