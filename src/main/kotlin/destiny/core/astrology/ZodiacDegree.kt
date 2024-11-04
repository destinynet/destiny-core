/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

import destiny.tools.CircleTools.aheadOf
import destiny.tools.CircleTools.normalize
import destiny.tools.getTitle
import java.io.Serializable
import java.util.*

interface IZodiacDegree : Serializable {
  val zDeg: Double

  val sign: ZodiacSign
    get() = ZodiacSign.of(zDeg)

  val signDegree: Pair<ZodiacSign, Double>
    get() = sign to zDeg % 30

  val intDeg: Int
    get() = (zDeg % 30).toInt()

  val intMin: Int
    get() = ((zDeg - zDeg.toInt()) * 60).toInt()
}

/** 黃道帶度數 */
@JvmInline
value class ZodiacDegree private constructor(val value: Double) : IZodiacDegree, Comparable<ZodiacDegree> {

  override val zDeg: Double
    get() = value

  fun getAngle(to: ZodiacDegree): Double {
    return getAngle(this.value, to.value)
  }

  fun getAngle(to: Double): Double {
    return getAngle(this.value, to)
  }

  /**
   * @return 計算 此點 是否在 [to] 的東邊 (度數小，為東邊) , true 就是東邊 , false 就是西邊(含對沖/合相)
   */
  fun isOriental(to: ZodiacDegree): Boolean {
    return if (value < to.value && to.value - value < 180)
      true
    else
      value > to.value && value - to.value > 180
  }


  /**
   * @return 計算 此點 是否在 [to] 的西邊 (度數大，為西邊) , true 就是西邊 , false 就是東邊(含對沖/合相)
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

  /**
   * 此度數是否被 [from] 與 [to] 包圍 ( 180度(不含) 以內)
   * 其中 oriental 者 inclusive , 另一邊 exclusive
   */
  fun between(from: ZodiacDegree, to: ZodiacDegree): Boolean {
    return if (from.isOriental(to)) {
      from.value == this.value ||
        from.isOriental(this) && this.isOriental(to)
    } else {
      to.value == this.value ||
        from.isOccidental(this) && this.isOccidental(to)
    }
  }

  override operator fun compareTo(other: ZodiacDegree): Int {
    return value.compareTo(other.value)
  }

  operator fun plus(other: ZodiacDegree): ZodiacDegree {
    return (value + other.value).toZodiacDegree()
  }

  operator fun plus(other: Int): ZodiacDegree {
    return (value + other).toZodiacDegree()
  }

  operator fun plus(other: Double): ZodiacDegree {
    return (value + other).toZodiacDegree()
  }

  operator fun minus(other: ZodiacDegree): ZodiacDegree {
    return (value - other.value).toZodiacDegree()
  }

  operator fun minus(other: Int): ZodiacDegree {
    return (value - other).toZodiacDegree()
  }

  operator fun minus(other: Double): ZodiacDegree {
    return (value - other).toZodiacDegree()
  }

  fun midPoint(other: ZodiacDegree): ZodiacDegree {
    val halfAngle = getAngle(other)/2

    return if (this.isOriental(other))
      (value + halfAngle).toZodiacDegree()
    else
      (other.value + halfAngle).toZodiacDegree()
  }

  override fun toString(): String {
    return buildString {
      append(value)
      append(" (")
      signDegree.also { (sign,signDeg) ->
        append(sign.getTitle(Locale.ENGLISH))
        append("/")
        append(signDeg.toString().take(5))
      }
      append(")")
    }
  }


  companion object {

    fun Number.toZodiacDegree(): ZodiacDegree {
      return ZodiacDegree(this.toDouble().normalize())
    }

    fun of(sign: ZodiacSign, degree: Double): ZodiacDegree {
      require(degree >= 0.0 && degree < 30)
      return ZodiacDegree(sign.degree + degree)
    }

    /**
     * @return 計算黃道帶上兩個度數的交角 , 其值必定小於等於 180度
     */
    fun getAngle(from: Double, to: Double): Double {
      return when {
        from - to >= 180  -> 360 - from + to
        from - to >= 0    -> from - to
        from - to >= -180 -> to - from
        else              -> from + 360 - to  // (from - to < -180)
      }
    }

    object OrientalComparator : Comparator<ZodiacDegree> {
      override fun compare(o1: ZodiacDegree, o2: ZodiacDegree): Int {
        return when {
          o1.value == o2.value -> 0
          o1.isOriental(o2)    -> -1
          else                 -> 1
        }
      }

    }
  }
}
