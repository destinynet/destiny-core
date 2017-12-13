/**
 * @author smallufo
 * Created on 2008/1/19 at 上午 5:59:26
 */
package destiny.astrology

import java.util.*

class ZodiacDegreeDecoratorEnglish : IZodiacDegreeDecorator {

  private var deg: Double = 0.toDouble()

  private val min: Int
    get() = ((deg - getDeg()) * 60).toInt()

  private val sec: Double
    get() = ((deg - getDeg()) * 60 - min) * 60

  override fun getOutputString(value: Double): String {
    val sign = ZodiacSign.getZodiacSign(value)
    this.deg = value - sign.degree

    return sign.toString(Locale.US) + " "  + getDeg() + "Deg " + min + "Min " + sec + "Sec"
  }

  override fun getSimpOutString(degree: Double): String {
    val sign = ZodiacSign.getZodiacSign(degree)
    this.deg = degree - sign.degree

    val sb = StringBuilder()
    if (getDeg() < 10)
      sb.append("0")
    sb.append(getDeg())
    sb.append(sign.getAbbreviation(Locale.US))
    if (min < 10)
      sb.append("0")
    sb.append(min)
    return sb.toString()
  }

  private fun getDeg(): Int {
    return deg.toInt()
  }

}
