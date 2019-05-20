/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.astrology

import destiny.core.IPattern
import java.io.Serializable

sealed class AstroPattern(override val name: String,
                          override val notes: String? = null,
                          open val points: Set<Point> = emptySet(),
                          open val score: Double? = null) : IPattern, Serializable {

  override fun toString(): String {
    return "AstroPattern(notes=$notes)"
  }

  data class 大三角(override val points: Set<Point>, val element: Element, override val score: Double?) : AstroPattern("大三角", "$points 在 ${element}向星座 形成大三角")

  data class 風箏(val head: Point, val wings: Set<Point>, val tail: Point, override val score: Double?) : AstroPattern("風箏", "$head 是風箏頭， $wings 是風箏翼 , $tail 是尾巴") {
    override val points: Set<Point>
      get() = wings.plus(head).plus(tail)
  }

  // T-Squared
  data class 三刑會沖(val oppoPoints: Set<Point>, val squaredPoint: Point, override val score: Double?) : AstroPattern("三刑會沖", "$oppoPoints 正沖，兩者均與 $squaredPoint 相刑") {
    override val points: Set<Point>
      get() = oppoPoints.plus(squaredPoint)
  }

  // 60 , 150 , 150
  data class 上帝之指(val bottoms: Set<Point>, val pointer: Point, val pointerSign: ZodiacSign, override val score: Double?) : AstroPattern("上帝之指", "$bottoms 與 $pointer 形成上帝之指 , 指向 $pointerSign") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer)
  }

  // 72 , 144 , 144
  data class 黃金指(val bottoms: Set<Point>, val pointer: Point, val pointedSign: ZodiacSign, override val score: Double?) : AstroPattern("黃金指", "$bottoms 與 $pointer 形成 黃金指 , 指向 $pointedSign") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer)
  }


  data class 大十字(override val points: Set<Point>, val quality: Quality, override val score: Double?) : AstroPattern("大十字", "$points 在 ${quality}宮形成大十字") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 大十字) return false

      if (points != other.points) return false
      if (quality != other.quality) return false

      return true
    }

    override fun hashCode(): Int {
      var result = points.hashCode()
      result = 31 * result + quality.hashCode()
      return result
    }
  }

  data class DoubleT(val tSquares: Set<三刑會沖> , override val score: Double?) : AstroPattern("DoubleT") {
    override val notes: String?
      get() {
        val (t1, t2) = tSquares.toList().let { it[0] to it[1] }
        return StringBuilder().apply {
          append(t1.oppoPoints.plus(t1.squaredPoint))
          append("與")
          append(t2.oppoPoints.plus(t2.squaredPoint))
          append("形成兩組 三刑會沖")
        }.toString()
      }

    override val points: Set<Point>
      get() = tSquares.flatMap { it.points }.toSet()
  }

  data class 六芒星(val grandTrines: Set<大三角>, override val score: Double?) : AstroPattern("六芒星") {
    override val notes: String?
      get() {
        val (g1, g2) = grandTrines.toList().let { it[0] to it[1] }
        return StringBuilder().apply {
          append(g1.points)
          append("與")
          append(g2.points)
          append("形成六芒星")
        }.toString()
      }
  }

  data class 楔子(val oppoPoints: Set<Point>, val moderator: Point, val moderatorSign: ZodiacSign , override val score: Double?) : AstroPattern("楔子", "$oppoPoints 對沖， $moderator 在 $moderatorSign 介入與彼此分別形成 拱 與 六合 , 化解對沖")

  /** 五個 [黃金指] */
  data class 五芒星(override val points: Set<Point> , override val score: Double?) : AstroPattern("五芒星", "$points 形成 五芒星")

  data class 聚集星座(override val points: Set<Point>, val sign: ZodiacSign) : AstroPattern("聚集星座", "${points.size}星聚集在 $sign : ${points.joinToString(",")}")
  data class 聚集宮位(override val points: Set<Point>, val house: Int) : AstroPattern("聚集宮位", "${points.size}星聚集在 第${house}宮 : ${points.joinToString(",")}")

}

interface IPatternFactory : Serializable {

  fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern>

}

