/**
 * Created by smallufo on 2023-04-23.
 */
package destiny.core.astrology

import mu.KotlinLogging
import kotlin.math.min

object HoroscopeFunctions {

  val logger = KotlinLogging.logger { }

  fun IHoroscopeModel.getAxisScore(planet: Planet, axisHouses: Set<Int> = setOf(1, 4, 7, 11)): Double? {

    return this.getPosition(planet)?.lngDeg?.let { planetDeg ->

      val (axis, from, to) = axisHouses.map { house ->
        house to when (house) {
          1    -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

          4    -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6)) / 2.0)

          7    -> this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6))) / 2.0 to
            this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9)) / 2.0)

          10   -> this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

          else -> throw IllegalArgumentException("Invalid house $house for axis")
        }
      }.map { (house, fromTo) ->
        val (from, to) = fromTo

        val between = planetDeg.between(from, to)
        Triple(house, between, fromTo)
      }.first { (house, between, fromTo) -> between }
        .let { (house, between, fromTo) ->
          Triple(house, fromTo.first, fromTo.second)
        }

      val axisDeg: ZodiacDegree = this.getCuspDegree(axis)
      val axisAngle: Double = axisDeg.getAngle(planetDeg)

      val fromAngle: Double = from.getAngle(planetDeg)
      val toAngle: Double = to.getAngle(planetDeg)
      val rangeAngle: Double = from.getAngle(to)

      val edgeAngle: Double = min(fromAngle, toAngle)

      logger.trace { "planetDeg = ${planetDeg.value} , axisDeg = ${axisDeg.value} , 影響力 from ${from.value} to ${to.value} , rangeAngle = $rangeAngle , axisAngle = $axisAngle , edgeAngle = $edgeAngle" }

      val score = if (planetDeg.between(axisDeg, from)) {
        1 - axisAngle / axisDeg.getAngle(from)
      } else {
        1 - axisAngle / axisDeg.getAngle(to)
      }

      logger.trace { "$planet 在 $axis 宮 的範圍內 .  距離中軸 ${axisAngle.toString().take(3)} 度 , range = ${rangeAngle.toInt()} , 得分 $score " }
      score
    }
  }

}

