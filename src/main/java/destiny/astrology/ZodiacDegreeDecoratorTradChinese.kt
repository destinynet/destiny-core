/**
 * @author smallufo
 * Created on 2008/1/14 at 下午 11:13:11
 */
package destiny.astrology

import java.util.Locale

class ZodiacDegreeDecoratorTradChinese : IZodiacDegreeDecorator {

  private var deg: Double = 0.toDouble()

  private val min: Int
    get() = ((deg - getDeg()) * 60).toInt()

  private val sec: Double
    get() = ((deg - getDeg()) * 60 - min) * 60

  override fun getOutputString(degree: Double): String {
    val sign = ZodiacSign.getZodiacSign(degree)
    this.deg = degree - sign.degree

    val sb = StringBuilder()
    sb.append(sign.toString(Locale.TAIWAN)).append(" ")
    if (getDeg() < 10)
      sb.append("0")
    sb.append(getDeg()).append("度 ")
    if (min < 10)
      sb.append("0")
    sb.append(min).append("分 ")
    if (sec < 10) {
      sb.append("0")
      sb.append(sec.toString().substring(0, 1))
    } else
      sb.append(sec.toString().substring(0, 2))
    sb.append("秒")
    return sb.toString()
  }

  override fun getSimpOutString(degree: Double): String {
    val sign = ZodiacSign.getZodiacSign(degree)
    this.deg = degree - sign.degree

    val sb = StringBuilder()
    if (getDeg() < 10)
      sb.append("0")
    sb.append(getDeg())
    sb.append(sign.getAbbreviation(Locale.TAIWAN))
    if (min < 10)
      sb.append("0")
    sb.append(min)
    return sb.toString()
  }

  private fun getDeg(): Int {
    return deg.toInt()
  }

}
