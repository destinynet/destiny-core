/**
 * Created by smallufo on 2023-04-23.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.EclipseTime
import destiny.core.astrology.eclipse.ILunarEclipse
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.IEvent
import destiny.core.calendar.SolarTermsEvent
import destiny.core.calendar.TimeDesc
import mu.KotlinLogging

object HoroscopeFunctions {

  val logger = KotlinLogging.logger { }

  private fun Axis.toHouse(): Int {
    return when (this) {
      Axis.RISING   -> 1
      Axis.NADIR    -> 4
      Axis.SETTING  -> 7
      Axis.MERIDIAN -> 10
    }
  }

  data class AxisEffect(val axis: Axis, val peak: Double = 1.0, val valley: Double = 0.0)

  /**
   * @param unAffected 未受影響的範圍內，評分為幾分
   */
  fun IHoroscopeModel.getAxisScore(
    planet: Planet,
    axisEffects: Set<AxisEffect> = Axis.list.map { AxisEffect(it) }.toSet(),
    rotatingDeg: Double = 0.0,
    unAffected: Double = 0.0
  ): Double? {

    return this.getPosition(planet)?.lngDeg?.let { planetDeg ->

      axisEffects.map { axisEffect ->
        axisEffect to when (axisEffect.axis) {
          Axis.RISING   -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

          Axis.NADIR    -> this.getCuspDegree(2) + (this.getCuspDegree(2).getAngle(this.getCuspDegree(3))) / 2.0 to
            this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6)) / 2.0)

          Axis.SETTING  -> this.getCuspDegree(5) + (this.getCuspDegree(5).getAngle(this.getCuspDegree(6))) / 2.0 to
            this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9)) / 2.0)

          Axis.MERIDIAN -> this.getCuspDegree(8) + (this.getCuspDegree(8).getAngle(this.getCuspDegree(9))) / 2.0 to
            this.getCuspDegree(11) + (this.getCuspDegree(11).getAngle(this.getCuspDegree(12)) / 2.0)

        }.let { (from, to) -> from.minus(rotatingDeg) to to.minus(rotatingDeg) }
      }
        .map { (house, fromTo) ->
          val (from, to) = fromTo

          val between = planetDeg.between(from, to)
          Triple(house, between, fromTo)
        }.firstOrNull { (house, between, fromTo) -> between }
        ?.let { (house, between, fromTo) ->
          Triple(house, fromTo.first, fromTo.second)
        }?.let { (axisEffect: AxisEffect, from, to) ->

          val axisDeg: ZodiacDegree = this.getCuspDegree(axisEffect.axis.toHouse()).minus(rotatingDeg)
          logger.trace { "${axisEffect.axis} deg = ${axisDeg.value} , from ${from.value} to ${to.value}" }
          val axisAngle: Double = axisDeg.getAngle(planetDeg)

          (1 - axisAngle / axisDeg.getAngle(
            if (planetDeg.between(axisDeg, from))
              from
            else
              to
          )) * (axisEffect.peak - axisEffect.valley) + axisEffect.valley


        } ?: unAffected

    }
  }

  fun List<IEvent>.toTimeDesc(fromGmt: GmtJulDay, toGmt: GmtJulDay): List<TimeDesc> {
    return this.flatMap { event: IEvent ->
      when (event) {
        // 節氣
        is SolarTermsEvent      -> setOf<TimeDesc>(TimeDesc.TypeSolarTerms(event.begin, event.solarTerms.toString(), event.solarTerms))
        // 逆行
        is Stationary -> setOf(
          when(event.type) {
            StationaryType.DIRECT_TO_RETROGRADE -> TimeDesc.Retrograde.Begin(event)
            StationaryType.RETROGRADE_TO_DIRECT -> TimeDesc.Retrograde.End(event)
          }
        )

        is TimeDesc -> setOf(event)

        // 日蝕
        is ISolarEclipse -> {
          buildSet {
            if (event.begin in fromGmt..toGmt) {
              add(TimeDesc.TypeSolarEclipse(event.begin, event.solarType, EclipseTime.BEGIN))
            }
            if (event.max in fromGmt..toGmt) {
              add(TimeDesc.TypeSolarEclipse(event.max, event.solarType, EclipseTime.MAX))
            }
            if (event.end in fromGmt..toGmt) {
              add(TimeDesc.TypeSolarEclipse(event.end, event.solarType, EclipseTime.END))
            }
          }
        }

        // 空亡
        is Misc.VoidCourseSpan -> {
          buildSet {
            if (event.begin in fromGmt .. toGmt) {
              add(TimeDesc.Void.Begin(event))
            }
            if (event.end in fromGmt .. toGmt) {
              add(TimeDesc.Void.End(event))
            }
          }
        }

        // 月蝕
        is ILunarEclipse -> {
          buildSet {
            if (event.begin in fromGmt..toGmt) {
              add(TimeDesc.TypeLunarEclipse(event.begin, event.lunarType, EclipseTime.BEGIN))
            }
            if (event.max in fromGmt..toGmt) {
              add(TimeDesc.TypeLunarEclipse(event.max, event.lunarType, EclipseTime.MAX))
            }
            if (event.end in fromGmt..toGmt) {
              add(TimeDesc.TypeLunarEclipse(event.end, event.lunarType, EclipseTime.END))
            }
          }
        }

        else                    -> emptySet()
      }
    }.sortedBy { it }
      .toList()
  }

}

