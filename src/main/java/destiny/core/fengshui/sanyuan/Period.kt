package destiny.core.fengshui.sanyuan

import destiny.core.ILoop
import destiny.core.fengshui.sanyuan.NineStar.Companion.toStar
import destiny.tools.ArrayTools
import kotlinx.serialization.Serializable

/**
 * 運 (1~9)
 */
@Serializable
enum class Period constructor(val value: Int) : ILoop<Period> , Comparable<Period> {
  P1(1), P2(2), P3(3), P4(4), P5(5), P6(6), P7(7), P8(8), P9(9);

  override fun next(n: Int): Period {
    return (value + n).toPeriod()
  }

  fun toNineStar() : NineStar {
    return value.toStar()
  }

  companion object {

    fun Int.toPeriod(): Period {
      return ArrayTools[values(), this - 1]
    }

    fun of(value: Int): Period {
      return value.toPeriod()
    }
  }
}
