/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:42:12
 */
package destiny.core.astrology

import destiny.tools.Decorator
import destiny.tools.truncateToString

class AzimuthDegreeEnglishDecorator : Decorator<Double> {
  override fun getOutputString(value: Double): String {

    return crossMap.asSequence().firstOrNull { (range, _) ->
      value in range
    }?.value ?: run {
      if (value <= 45)
        "E by N " + value.truncateToString(1)
      else if (value > 45 && value < 90)
        "N by E " + (90 - value).truncateToString(1)
      else if (value > 90 && value <= 135)
        "S by E " + (value - 90).truncateToString(1)
      else if (value > 135 && value < 180)
        "E by S " + (180 - value).truncateToString(1)
      else if (value > 180 && value <= 225)
        "W by S " + (value - 180).truncateToString(1)
      else if (value > 225 && value < 270)
        "S by W " + (270 - value).truncateToString(1)
      else if (value > 270 && value <= 315)
        "N by W " + (value - 270).truncateToString(1)
      else
        "W by N " + (360 - value).truncateToString(1)
    }
  }

  companion object {
    private val crossMap = mapOf(
      (0.0..0.5) to "North",
      (359.5..360.0) to "North",
      (89.5..90.5) to "East",
      (179.5..180.5) to "South",
      (269.5..270.5) to "West",
    )
  }
}
