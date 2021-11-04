/**
 * @author smallufo
 * Created on 2007/6/22 at 上午 5:14:53
 */
package destiny.core.astrology

import java.util.*
import kotlin.reflect.KClass

sealed class Asteroid(nameKey: String,
                      abbrKey: String,
                      val index: Int ,
                      unicode : Char? = null) : Star(nameKey, abbrKey, Star::class.java.name , unicode), Comparable<Asteroid> {

  /** 穀神星  */
  object CERES : Asteroid("Asteroid.CERES", "Asteroid.CERES_ABBR", 1 , '⚳')

  /** 智神星  */
  object PALLAS : Asteroid("Asteroid.PALLAS", "Asteroid.PALLAS_ABBR", 2 , '⚴')

  /** 婚神星  */
  object JUNO : Asteroid("Asteroid.JUNO", "Asteroid.JUNO_ABBR", 3 , '⚵')

  /** 灶神星  */
  object VESTA : Asteroid("Asteroid.VESTA", "Asteroid.VESTA_ABBR", 4 , '⚶')

  /** 凱龍星  */
  object CHIRON : Asteroid("Asteroid.CHIRON", "Asteroid.CHIRON_ABBR", 2060 , '⚷')

  /** 人龍星  */
  object PHOLUS : Asteroid("Asteroid.PHOLUS", "Asteroid.PHOLUS_ABBR", 5145 , '⯛')


  override fun compareTo(other: Asteroid): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }

  companion object : IPoint<Asteroid> {

    override val type: KClass<out Point> = Asteroid::class

    override val values by lazy {
      arrayOf(CERES, PALLAS, JUNO, VESTA, CHIRON, PHOLUS)
    }
    val list by lazy { listOf(*values) }

    override fun fromString(value : String) : Asteroid? {
      return values.firstOrNull {
        it.toString(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }
  }
}
