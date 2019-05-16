/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.astrology

import com.google.common.collect.Sets
import destiny.core.IPattern
import java.io.Serializable

sealed class AstroPattern(override val name: String,
                          override val notes: String? = null,
                          open val points: Set<Point> = emptySet()) : IPattern, Serializable {

  override fun toString(): String {
    return "AstroPattern(notes=$notes)"
  }

  data class 大三角(override val points: Set<Point>, val element: Element) : AstroPattern("大三角", "$points 在 ${element}向星座 形成大三角")

  data class 風箏(val head: Point, val wings: Set<Point>, val tail: Point) : AstroPattern("風箏", "$head 是風箏頭， $wings 是風箏翼 , $tail 是尾巴") {
    override val points: Set<Point>
      get() = wings.plus(head).plus(tail)
  }

  // T-Squared
  data class 三刑會沖(val oppoPoints: Set<Point>, val squaredPoint: Point) : AstroPattern("三刑會沖", "$oppoPoints 正沖，兩者均與 $squaredPoint 相刑") {
    override val points: Set<Point>
      get() = oppoPoints.plus(squaredPoint)
  }

  // 60 , 150 , 150
  data class 上帝之指(val bottoms: Set<Point>, val pointer: Point) : AstroPattern("上帝之指", "$bottoms 與 $pointer 形成上帝之指") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer)
  }

  // 72 , 144 , 144
  data class 黃金指(val bottoms: Set<Point>, val pointer: Point, val pointedSign: ZodiacSign) : AstroPattern("黃金指", "$bottoms 與 $pointer 形成 黃金指 , 指向 $pointedSign") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer)
  }


  data class 大十字(override val points: Set<Point>, val quality: Quality) : AstroPattern("大十字", "$points 在 ${quality}宮形成大十字")

  data class DoubleT(val tSquares: Set<三刑會沖>) : AstroPattern("DoubleT") {
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

  data class 六芒星(val grandTrines: Set<大三角>) : AstroPattern("六芒星") {
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

  data class 楔子(val oppoPoints: Set<Point>, val moderator: Point) : AstroPattern("楔子", "$oppoPoints 對沖， $moderator 介入與彼此分別形成 拱 與 六合 , 化解對沖")

  data class 五芒星(override val points: Set<Point>) : AstroPattern("五芒星", "$points 形成 五芒星")

}

interface IPatternFactory : Serializable {

  fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern>

}

