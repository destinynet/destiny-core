/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

import destiny.tools.CircleTools.aheadOf
import destiny.tools.CircleTools.normalize


inline class ZodiacDegree(val value: Double) {

  fun getZodiacSign() : ZodiacSign {
    return ZodiacSign.of(value)
  }

  fun getSignAndDegree() : Pair<ZodiacSign, Double> {
    return getZodiacSign() to value % 30
  }

  fun getAngle(to : ZodiacDegree) : Double {
    return when {
      value - to.value >= 180 -> 360 - value + to.value
      value - to.value >= 0 -> value - to.value
      value - to.value >= -180 -> to.value - value
      else -> value + 360 - to.value  // (from - to < -180)
    }
  }

  /**
   * @return 計算 此點 是否在 to 的東邊 (度數小，為東邊) , true 就是東邊 , false 就是西邊(含對沖/合相)
   */
  fun isOriental(to: ZodiacDegree): Boolean {
    return if (value < to.value && to.value - value < 180)
      true
    else
      value > to.value && value - to.value > 180
  }


  /**
   * @return 計算 此點 是否在 to 的西邊 (度數大，為西邊) , true 就是西邊 , false 就是東邊(含對沖/合相)
   */
  fun isOccidental(to: ZodiacDegree): Boolean {
    return if (value < to.value && to.value - value > 180)
      true
    else
      value > to.value && value - to.value < 180
  }

  fun normalize(): ZodiacDegree {
    return when {
      value >= 360 || value < 0 -> ZodiacDegree(value.normalize())
      else                      -> this
    }
  }

  fun aheadOf(other: ZodiacDegree): Double {
    return value.aheadOf(other.value)
  }

  operator fun compareTo(other: ZodiacDegree): Int {
    val v = value - other.value
    return when {
      v == 0.0  -> 0
      value < 0 -> -1
      else      -> 1
    }
  }

  operator fun plus(other: ZodiacDegree): Double {
    return value + other.value
  }
}
