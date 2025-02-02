/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.core.toString
import destiny.tools.serializers.astrology.AxisSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

@Serializable(with = AxisSerializer::class)
sealed class Axis(nameKey: String, abbrKey: String) : AstroPoint(nameKey, Axis::class.java.name , abbrKey) {

  object RISING   : Axis("Axis.RISING"  , "Axis.RISING_ABBR")
  object SETTING  : Axis("Axis.SETTING" , "Axis.SETTING_ABBR")
  object MERIDIAN : Axis("Axis.MERIDIAN", "Axis.MERIDIAN_ABBR")
  object NADIR    : Axis("Axis.NADIR"   , "Axis.NADIR_ABBR")

  companion object : IPoints<Axis> {

    override val type: KClass<out Point> = Axis::class

    val array by lazy { arrayOf(RISING, SETTING, MERIDIAN, NADIR) }
    val list by lazy { listOf(*array)}

    override val values: Array<Axis> by lazy { array }

    override fun fromString(value: String, locale: Locale): Axis? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }

  }
}
