/**
 * @author smallufo
 * Created on 2008/1/15 at 上午 12:55:13
 */
package destiny.core.astrology

import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.LocalizedDecorator
import destiny.tools.LocaleTools
import destiny.tools.getOutputString
import destiny.tools.getTitle

object ZodiacDegreeDecorator : LocalizedDecorator<Double> {
  private val implMap = mapOf(
    Lang.ZH_TW to ZodiacDegreeDecoratorTradChinese(),
    Lang.EN_US to ZodiacDegreeDecoratorEnglish()
  )


  override fun getOutputString(degree: Double, lang: Lang): String {
    return implMap.getOutputString(degree, lang)
  }

  fun getSimpOutputString(degree: Double, lang: Lang): String {
    return implMap.getValue(LangTools.getBestMatchingLangOrFirst(lang, implMap.keys)).getSimpOutString(degree)
  }
}

private val commonSimpleDegToString = { sb: StringBuilder, degree: Double, lang: Lang, min: Int ->
  val sign = ZodiacSign.of(degree)
  val degInt = (degree - sign.degree).toInt()

  if (degInt < 10)
    sb.append("0")
  sb.append(degInt)
  sb.append(sign.getAbbreviation(lang))
  if (min < 10)
    sb.append("0")
  sb.append(min)
}


class ZodiacDegreeDecoratorTradChinese : IZodiacDegreeDecorator {

  override val lang: Lang = Lang.ZH_TW

  private var deg: Double = 0.toDouble()

  private val min: Int
    get() = ((deg - getDeg()) * 60).toInt()

  private val sec: Double
    get() = ((deg - getDeg()) * 60 - min) * 60

  override fun getOutputString(value: Double): String {
    val sign = ZodiacSign.of(value)
    this.deg = value - sign.degree

    val sb = StringBuilder()

    sb.append(sign.getTitle(lang)).append(" ")
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
    val sign = ZodiacSign.of(degree)
    this.deg = degree - sign.degree

    val sb = StringBuilder()
    commonSimpleDegToString(sb, degree, lang, min)
    return sb.toString()
  }

  private fun getDeg(): Int {
    return deg.toInt()
  }

} // Chinese


class ZodiacDegreeDecoratorEnglish : IZodiacDegreeDecorator {

  override val lang: Lang = Lang.EN_US

  private var deg: Double = 0.toDouble()

  private val min: Int
    get() = ((deg - getDeg()) * 60).toInt()

  private val sec: Double
    get() = ((deg - getDeg()) * 60 - min) * 60

  override fun getOutputString(value: Double): String {
    val sign = ZodiacSign.of(value)
    this.deg = value - sign.degree

    return sign.getTitle(lang) + " " + getDeg() + "Deg " + min + "Min " + sec + "Sec"
  }

  override fun getSimpOutString(degree: Double): String {
    val sign = ZodiacSign.of(degree)
    this.deg = degree - sign.degree

    val sb = StringBuilder()
    commonSimpleDegToString.invoke(sb, degree, lang, min)
    return sb.toString()
  }

  private fun getDeg(): Int {
    return deg.toInt()
  }

} // English
