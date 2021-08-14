/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 7:39:09
 */
package destiny.core.astrology

import java.util.*


/** 漢堡學派 Uranian Astrology  */
sealed class Hamburger (nameKey: String) : Star(nameKey, Star::class.qualifiedName!!), Comparable<Hamburger> {

  object CUPIDO   : Hamburger("Hamburger.CUPIDO")
  object HADES    : Hamburger("Hamburger.HADES")
  object ZEUS     : Hamburger("Hamburger.ZEUS")
  object KRONOS   : Hamburger("Hamburger.KRONOS")
  object APOLLON  : Hamburger("Hamburger.APOLLON")
  object ADMETOS  : Hamburger("Hamburger.ADMETOS")
  object VULKANUS : Hamburger("Hamburger.VULKANUS")
  object POSEIDON : Hamburger("Hamburger.POSEIDON")

  override fun compareTo(other: Hamburger): Int {
    if (this == other)
      return 0

    return array.indexOf(this) - array.indexOf(other)
  }


  companion object {
    val array by lazy {
      arrayOf(CUPIDO, HADES, ZEUS, KRONOS, APOLLON, ADMETOS,
              VULKANUS, POSEIDON)
    }

    val list by lazy {
      listOf(*array)
    }

    fun fromString(value : String) : Hamburger? {
      return array.firstOrNull {
        it.toString(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }

  }
}
