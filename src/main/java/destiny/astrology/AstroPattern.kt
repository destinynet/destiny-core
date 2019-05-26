/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.astrology

import destiny.core.IPattern
import java.io.Serializable

/**
 * 某星( or [Point] ) 位於那個星座 , 第幾宮 , 通常用於描述 pattern 的關鍵點
 */
data class PointSignHouse(val point: Point,
                          val sign: ZodiacSign,
                          val house: Int)

sealed class AstroPattern(override val name: String,
                          override val notes: String? = null,
                          open val points: Set<Point> = emptySet(),
                          open val score: Double? = null) : IPattern, Serializable {

  override fun toString(): String {
    return "AstroPattern(notes=$notes)"
  }

  data class 大三角(override val points: Set<Point>, val element: Element, override val score: Double?) : AstroPattern("大三角", "$points 在 ${element}向星座 形成大三角") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 大三角) return false

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

  data class 風箏(val head: PointSignHouse, val wings: Set<Point>, val tail: PointSignHouse, override val score: Double?) : AstroPattern("風箏", "${head.point} 是風箏頭， $wings 是風箏翼 , ${tail.point} 是尾巴") {
    override val points: Set<Point>
      get() = wings.plus(head.point).plus(tail.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 風箏) return false

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

  // T-Squared
  data class 三刑會沖(val oppoPoints: Set<Point>, val squared : PointSignHouse , override val score: Double?) : AstroPattern("三刑會沖", "$oppoPoints 正沖，兩者均與 ${squared.point} 相刑") {
    override val points: Set<Point>
      get() = oppoPoints.plus(squared.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 三刑會沖) return false

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

  // 60 , 150 , 150
  data class 上帝之指(val bottoms: Set<Point>, val pointer: PointSignHouse, override val score: Double?) : AstroPattern("上帝之指", "$bottoms 與 ${pointer.point} 形成上帝之指 , 指向 ${pointer.sign}") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 上帝之指) return false

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

  /** [上帝之指] + 對沖點 (與雙翼形成 30度) */
  data class 回力鏢(val fingerOfGod: 上帝之指, val oppoPoint: PointSignHouse, override val score: Double?) : AstroPattern("回力鏢", "${fingerOfGod.points}形成 上帝之指 , 加入 ${oppoPoint.point} 形成 回力鏢") {
    override val points: Set<Point>
      get() = fingerOfGod.points.plus(oppoPoint.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 回力鏢) return false

      if (fingerOfGod != other.fingerOfGod) return false
      if (oppoPoint != other.oppoPoint) return false

      return true
    }

    override fun hashCode(): Int {
      var result = fingerOfGod.hashCode()
      result = 31 * result + oppoPoint.hashCode()
      return result
    }
  }

  // 72 , 144 , 144
  data class 黃金指(val bottoms: Set<Point>, val pointer: PointSignHouse, override val score: Double?) : AstroPattern("黃金指", "$bottoms 與 ${pointer.point} 形成 黃金指 , 指向 ${pointer.sign}") {
    override val points: Set<Point>
      get() = bottoms.plus(pointer.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 黃金指) return false

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

  data class DoubleT(val tSquares: Set<三刑會沖>, override val score: Double?) : AstroPattern("DoubleT") {
    override val notes: String?
      get() {
        val (t1, t2) = tSquares.toList().let { it[0] to it[1] }
        return StringBuilder().apply {
          append(t1.oppoPoints.plus(t1.squared.point))
          append("與")
          append(t2.oppoPoints.plus(t2.squared.point))
          append("形成兩組 三刑會沖")
        }.toString()
      }

    override val points: Set<Point>
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

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 六芒星) return false

      if (grandTrines != other.grandTrines) return false

      return true
    }

    override fun hashCode(): Int {
      return grandTrines.hashCode()
    }
  }

  /**
   * 180 沖 , 逢 第三顆星 , 以 60/120 介入，緩和局勢
   */
  data class 楔子(val oppoPoints: Set<Point>, val moderator: PointSignHouse, override val score: Double?) : AstroPattern("楔子", "$oppoPoints 對沖， ${moderator.point} 在 ${moderator.sign} 介入與彼此分別形成 拱 與 六合 , 化解對沖") {
    override val points: Set<Point>
      get() = oppoPoints.plus(moderator.point)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 楔子) return false

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
   * 兩組 [楔子] 對沖，兩個60度，兩個120度，這也會形成壓力，但是彼此間又可以釋放壓力，非常詭異
   * @param points 總共4顆星
   */
  data class 神秘長方形(override val points: Set<Point>, override val score: Double?) : AstroPattern("神秘長方形", "$points 四星 形成 神秘長方形") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 神秘長方形) return false

      if (points != other.points) return false

      return true
    }

    override fun hashCode(): Int {
      return points.hashCode()
    }
  }

  /**
   * 五個 [黃金指]
   * @param points 總共5顆星
   * */
  data class 五芒星(override val points: Set<Point>, override val score: Double?) : AstroPattern("五芒星", "$points 形成 五芒星") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 五芒星) return false

      if (points != other.points) return false

      return true
    }

    override fun hashCode(): Int {
      return points.hashCode()
    }
  }

  data class 聚集星座(override val points: Set<Point>, val sign: ZodiacSign, override val score: Double?) : AstroPattern("聚集星座", "${points.size}星聚集在 $sign : ${points.joinToString(",")}") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 聚集星座) return false

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

  data class 聚集宮位(override val points: Set<Point>, val house: Int, override val score: Double) : AstroPattern("聚集宮位", "${points.size}星聚集在 第${house}宮 : ${points.joinToString(",")}") {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 聚集宮位) return false

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
   * 兩組 三顆星以上的合相星群 彼此對沖
   */
  data class 對峙(val clusters: Set<Set<Point>>, override val score: Double?) : AstroPattern("星群對峙", clusters.joinToString("與") + "對峙") {
    override val points: Set<Point>
      get() = clusters.flatten().toSet()

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is 對峙) return false

      if (clusters != other.clusters) return false

      return true
    }

    override fun hashCode(): Int {
      return clusters.hashCode()
    }
  }

}

interface IPatternFactory : Serializable {

  fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern>

}

