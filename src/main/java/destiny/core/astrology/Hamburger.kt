/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 7:39:09
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.toString
import java.util.*
import kotlin.reflect.KClass


/** 漢堡學派 Uranian Astrology  */
sealed class Hamburger(nameKey: String) : Star(nameKey, Star::class.qualifiedName!!), Comparable<Hamburger> {

  object CUPIDO : Hamburger("Hamburger.CUPIDO")
  object HADES : Hamburger("Hamburger.HADES")
  object ZEUS : Hamburger("Hamburger.ZEUS")
  object KRONOS : Hamburger("Hamburger.KRONOS")
  object APOLLON : Hamburger("Hamburger.APOLLON")
  object ADMETOS : Hamburger("Hamburger.ADMETOS")
  object VULKANUS : Hamburger("Hamburger.VULKANUS")
  object POSEIDON : Hamburger("Hamburger.POSEIDON")

  override fun compareTo(other: Hamburger): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }


  companion object : IPoints<Hamburger> {

    override val type: KClass<out AstroPoint> = Hamburger::class

    override val values by lazy {
      arrayOf(
        CUPIDO, HADES, ZEUS, KRONOS, APOLLON, ADMETOS,
        VULKANUS, POSEIDON
      )
    }

    val list by lazy {
      listOf(*values)
    }

    override fun fromString(value: String, locale: Locale): Hamburger? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }

  }
}
