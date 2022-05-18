/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.core.astrology

import java.io.Serializable
import java.util.*

/**
 * 某星( or [AstroPoint] ) 位於那個星座 , 第幾宮 , 通常用於描述 pattern 的關鍵點
 *
 * http://goodvibeastrology.com/aspect-patterns/
 */
data class PointSignHouse(val point: AstroPoint,
                          val sign: ZodiacSign,
                          val house: Int)

sealed class AstroPattern(open val points: Set<AstroPoint> = emptySet(),
                          open val score: Double? = null) : IAstroPattern, Serializable {

  /**
   * [GrandTrine] : 大三角
   */
  data class GrandTrine(override val points: Set<AstroPoint>, val element: Element, override val score: Double? = null) : AstroPattern() {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GrandTrine) return false

      if (points != other.points) return false
      if (element != other.element) return false

      return true
    }

    override fun hashCode(): Int {
      var result = points.hashCode()
      result = 31 * result + element.hashCode()
      return result
    }
  }

  /**
   * [Kite] : 風箏
   */
  data class Kite(val head: PointSignHouse, val wings: Set<AstroPoint>, val tail: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = wings.plus(head.point).plus(tail.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Kite) return false

      if (head != other.head) return false
      if (wings != other.wings) return false
      if (tail != other.tail) return false

      return true
    }

    override fun hashCode(): Int {
      var result = head.hashCode()
      result = 31 * result + wings.hashCode()
      result = 31 * result + tail.hashCode()
      return result
    }
  }

  /**
   * [TSquared] : 三刑會沖
   */
  data class TSquared(val oppoPoints: Set<AstroPoint>, val squared: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = oppoPoints.plus(squared.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is TSquared) return false

      if (oppoPoints != other.oppoPoints) return false
      if (squared != other.squared) return false

      return true
    }

    override fun hashCode(): Int {
      var result = oppoPoints.hashCode()
      result = 31 * result + squared.hashCode()
      return result
    }
  }

  /**
   * [Yod] : 上帝之指 , Finger of God
   * 60 , 150 , 150
   * */
  data class Yod(val bottoms: Set<AstroPoint>, val pointer: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = bottoms.plus(pointer.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Yod) return false

      if (bottoms != other.bottoms) return false
      if (pointer != other.pointer) return false

      return true
    }

    override fun hashCode(): Int {
      var result = bottoms.hashCode()
      result = 31 * result + pointer.hashCode()
      return result
    }
  }

  /**
   * [Boomerang] : 回力鏢
   * [Yod] + 對沖點 (與雙翼形成 30度)
   * */
  data class Boomerang(val yod: Yod, val oppoPoint: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = yod.points.plus(oppoPoint.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Boomerang) return false

      if (yod != other.yod) return false
      if (oppoPoint != other.oppoPoint) return false

      return true
    }

    override fun hashCode(): Int {
      var result = yod.hashCode()
      result = 31 * result + oppoPoint.hashCode()
      return result
    }
  }

  /**
   * [GoldenYod] : 黃金指 72 , 144 , 144
   * */
  data class GoldenYod(val bottoms: Set<AstroPoint>, val pointer: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = bottoms.plus(pointer.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GoldenYod) return false

      if (bottoms != other.bottoms) return false
      if (pointer != other.pointer) return false

      return true
    }

    override fun hashCode(): Int {
      var result = bottoms.hashCode()
      result = 31 * result + pointer.hashCode()
      return result
    }
  }


  /**
   * [GrandCross] : 大十字
   */
  data class GrandCross(override val points: Set<AstroPoint>, val quality: Quality, override val score: Double? = null) : AstroPattern() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is GrandCross) return false

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

  /**
   * [DoubleT] : 兩組 三刑會沖 (但未形成 [GrandCross]大十字 )
   */
  data class DoubleT(val tSquares: Set<TSquared>, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = tSquares.flatMap { it.points }.toSet()

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is DoubleT) return false

      if (tSquares != other.tSquares) return false

      return true
    }

    override fun hashCode(): Int {
      return tSquares.hashCode()
    }
  }

  /**
   * [Hexagon] : 六芒星 (兩組 [GrandTrine]大三角 , 彼此交角60度 )
   */
  data class Hexagon(val grandTrines: Set<GrandTrine>, override val score: Double? = null) : AstroPattern() {
    override fun getNotes(locale: Locale): String {
        val (g1, g2) = grandTrines.toList().let { it[0] to it[1] }
        return StringBuilder().apply {
          append(g1.points)
          append("與")
          append(g2.points)
          append("形成六芒星")
        }.toString()
      }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Hexagon) return false

      if (grandTrines != other.grandTrines) return false

      return true
    }

    override fun hashCode(): Int {
      return grandTrines.hashCode()
    }
  }

  /**
   * [Wedge] : 楔子
   * 180 沖 , 逢 第三顆星 , 以 60/120 介入，緩和局勢
   */
  data class Wedge(val oppoPoints: Set<AstroPoint>, val moderator: PointSignHouse, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = oppoPoints.plus(moderator.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Wedge) return false

      if (oppoPoints != other.oppoPoints) return false
      if (moderator != other.moderator) return false

      return true
    }

    override fun hashCode(): Int {
      var result = oppoPoints.hashCode()
      result = 31 * result + moderator.hashCode()
      return result
    }
  }

  /**
   * [MysticRectangle] : 神秘長方形
   * 兩組 [Wedge] 對沖，兩個60度，兩個120度，這也會形成壓力，但是彼此間又可以釋放壓力，非常詭異
   * @param points 總共4顆星
   */
  data class MysticRectangle(override val points: Set<AstroPoint>, override val score: Double? = null) : AstroPattern() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is MysticRectangle) return false

      if (points != other.points) return false

      return true
    }

    override fun hashCode(): Int {
      return points.hashCode()
    }
  }

  /**
   * [Pentagram] : 五芒星 五個 [GoldenYod]
   * @param points 總共5顆星
   * */
  data class Pentagram(override val points: Set<AstroPoint>, override val score: Double? = null) : AstroPattern() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Pentagram) return false

      if (points != other.points) return false

      return true
    }

    override fun hashCode(): Int {
      return points.hashCode()
    }
  }

  /**
   * [StelliumSign] : 聚集星座 (至少四顆星)
   */
  data class StelliumSign(override val points: Set<AstroPoint>, val sign: ZodiacSign, override val score: Double? = null) : AstroPattern() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is StelliumSign) return false

      if (points != other.points) return false
      if (sign != other.sign) return false

      return true
    }

    override fun hashCode(): Int {
      var result = points.hashCode()
      result = 31 * result + sign.hashCode()
      return result
    }
  }

  /**
   * [StelliumHouse] : 聚集宮位 (至少四顆星)
   */
  data class StelliumHouse(override val points: Set<AstroPoint>, val house: Int, override val score: Double? = null) : AstroPattern() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is StelliumHouse) return false

      if (points != other.points) return false
      if (house != other.house) return false

      return true
    }

    override fun hashCode(): Int {
      var result = points.hashCode()
      result = 31 * result + house
      return result
    }
  }

  /**
   * [Confrontation] 對峙
   * 兩組 三顆星以上的合相星群 彼此對沖
   */
  data class Confrontation(val clusters: Set<Set<AstroPoint>>, override val score: Double? = null) : AstroPattern() {
    override val points: Set<AstroPoint>
      get() = clusters.flatten().toSet()

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Confrontation) return false

      if (clusters != other.clusters) return false

      return true
    }

    override fun hashCode(): Int {
      return clusters.hashCode()
    }
  }

}

interface IPatternFactory : Serializable {

  fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern>

}

