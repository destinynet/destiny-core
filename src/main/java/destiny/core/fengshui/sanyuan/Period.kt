package destiny.core.fengshui.sanyuan

import destiny.tools.ArrayTools
import kotlinx.serialization.Serializable

/**
 * 運 (1~9)
 */
@JvmInline
@Serializable
value class Period private constructor(val value: Int) : Comparable<Period> {
  init {
    require(value >= 1)
    require(value <= 9)
  }

  override fun compareTo(other: Period): Int {
    return if (value == other.value)
      0
    else if (value - other.value < 0) -1 else 1
  }

  operator fun plus(count: Int): Period {
    return (this.value + count).toPeriod()
  }

  operator fun minus(count: Int): Period {
    return (this.value - count).toPeriod()
  }

  companion object {

    private val array by lazy {
      arrayOf(Period(1), Period(2), Period(3), Period(4), Period(5), Period(6), Period(7), Period(8), Period(9))
    }

    fun Int.toPeriod(): Period {
      return ArrayTools[array, this - 1]
    }

    fun of(value: Int): Period {
      return value.toPeriod()
    }
  }
}
