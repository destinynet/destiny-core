/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 7:39:09
 */
package destiny.astrology


/** 漢堡學派 Uranian Astrology  */
object Hamburgers {
  val array = arrayOf(Hamburger.CUPIDO, Hamburger.HADES, Hamburger.ZEUS, Hamburger.KRONOS, Hamburger.APOLLON, Hamburger.ADMETOS, Hamburger.VULKANUS, Hamburger.POSEIDON)
  val list = listOf(*array)
}

sealed class Hamburger (nameKey: String) : Star(nameKey, Star::class.java.name), Comparable<Hamburger> {

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

    return Hamburgers.array.indexOf(this) - Hamburgers.array.indexOf(other)
  }

}
