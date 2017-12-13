/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:42:12
 */
package destiny.astrology

import destiny.tools.Decorator

class AzimuthDegreeEnglishDecorator : Decorator<Double> {
  override fun getOutputString(value: Double): String {
    return if (value <= 0.5 || value >= 359.5)
      "North"
    else if (value in 89.5..90.5)
      "East"
    else if (value in 179.5..180.5)
      "South"
    else if (value in 269.5..270.5)
      "West"
    else {
      if (value <= 45)
        "E by N " + value.toString().substring(0, 4)
      else if (value > 45 && value < 90)
        "N by E " + (90 - value).toString().substring(0, 4)
      else if (value > 90 && value <= 135)
        "S by E " + (value - 90).toString().substring(0, 4)
      else if (value > 135 && value < 180)
        "E by S " + (180 - value).toString().substring(0, 4)
      else if (value > 180 && value <= 225)
        "W by S " + (value - 180).toString().substring(0, 4)
      else if (value > 225 && value < 270)
        "S by W " + (270 - value).toString().substring(0, 4)
      else if (value > 270 && value <= 315)
        "N by E " + (value - 270).toString().substring(0, 4)
      else
        "W by N " + (360 - value).toString().substring(0, 4)
    }
  }

}
