/**
 * Created by smallufo on 2023-02-23.
 */
package destiny.tools

import java.io.Serializable

@kotlinx.serialization.Serializable
@JvmInline
value class Score(val value: Double) : Comparable<Score>, Serializable {
  init {
    require(value in 0.0..1.0) {
      throw IllegalArgumentException("value $value 必須介於 0 與 1 之間")
    }
  }
  override fun compareTo(other: Score): Int {
    return if (value == other.value)
      0
    else if (value - other.value < 0) -1 else 1
  }


  operator fun minus(amount: Number): Score {
    return (value - amount.toDouble()).toScore()
  }

  operator fun plus(amount: Number): Score {
    return (value + amount.toDouble()).toScore()
  }

  operator fun plus(amount: Score): Double {
    return value + amount.value
  }

  operator fun minus(v: Score): Double {
    return value - v.value
  }


  companion object {
    fun Number.toScore(): Score {
      return Score(this.toDouble())
    }

    fun Iterable<Score>.average(): Score {
      val total = this.fold(0.0) { sum, score -> sum + score.value }
      return (total / this.count()).toScore()
    }
  }
}

