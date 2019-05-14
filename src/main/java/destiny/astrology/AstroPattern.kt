/**
 * Created by smallufo on 2019-05-12.
 */
package destiny.astrology

import com.google.common.collect.Sets
import destiny.core.IPattern
import java.io.Serializable

sealed class AstroPattern(override val name: String,
                          override val notes: String? = null) : IPattern, Serializable {

  override fun toString(): String {
    return "AstroPattern(notes=$notes)"
  }

  data class 大三角(val points: Set<Point>, val element: Element) : AstroPattern("大三角", "$points 在 ${element}向星座 形成大三角")

  data class 風箏(val head: Point, val wings: Set<Point>, val tail: Point) : AstroPattern("風箏", "$head 是風箏頭， $wings 是風箏翼 , $tail 是尾巴")

  // T-Squared
  data class 三刑會沖(val oppoPoints: Set<Point>, val squaredPoint: Point) : AstroPattern("三刑會沖", "$oppoPoints 正沖，兩者均與 $squaredPoint 相刑")

  data class 三刑會衝逢解(val embed: 三刑會沖, val point: Point) : AstroPattern("三刑會衝逢解", "$embed , 逢 $point 化解")

  data class 上帝之指(val points: Set<Point>, val quincunx: Point) : AstroPattern("上帝之指", "$points 與 $quincunx 形成上帝之手")

  data class 大十字(val points: Set<Point>, val quality: Quality) : AstroPattern("大十字", "$points 在 ${quality}宮形成大十字")
}

interface IPatternFactory : Serializable {

  fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern>

}

object AstroPatterns {

  //val classicalList = Planet.classicalList

  val aspectEffective = AspectEffectiveModern()

  // 90
  val squareCalculator = modernCalculator {
    aspects = setOf(Aspect.SQUARE)
  }

  val sextileCalculator = modernCalculator {
    aspects = setOf(Aspect.SEXTILE)
  }

  // 60,120
  val sextileTrineCalculator = modernCalculator {
    aspects = setOf(Aspect.SEXTILE, Aspect.TRINE)
  }

  // 只計算 0,60,90,120,180 五個重要交角
  private val highAspectsCalculator = modernCalculator {
    aspects = Aspect.getAngles(Aspect.Importance.HIGH)
  }

  //  0,60,90,120,180 + 30,45,135,150
  private val mediumHighAspectsCalculator = modernCalculator {
    aspects = Aspect.getAngles(Aspect.Importance.HIGH).plus(Aspect.getAngles(Aspect.Importance.MEDIUM))
  }


  val trineCalculator = HoroscopeAspectsCalculator(modernCalculator {
    aspects = setOf(Aspect.TRINE)
  })
  val a = modernCalculator {
    aspects = setOf(Aspect.OPPOSITION)
  }


  val highCalculator = HoroscopeAspectsCalculator(highAspectsCalculator)
  val mediumHighCalculator = HoroscopeAspectsCalculator(mediumHighAspectsCalculator)

  object GrandTrine : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {
      return trineCalculator.getAspectDataSet(starPosMap, Planet.list)
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->

          return Sets.combinations(dataSet, 3).map { threeSets ->
            val (set1, set2, set3) = threeSets.toList().let { Triple(it[0], it[1], it[2]) }
            set1.twoPoints.union(set2.twoPoints).union(set3.twoPoints)
              .takeIf { unionPoints -> unionPoints.size == 3 }
              ?.takeIf { unionPoints ->
                Sets.combinations(unionPoints, 2).all { twoPoint ->
                  val (p1, p2) = twoPoint.toList().let { it[0] to it[1] }
                  aspectEffective.isEffective(p1, starPosMap.getValue(p1).lng, p2, starPosMap.getValue(p2).lng, Aspect.TRINE)
                }
              }
              ?.let { unionPoints ->
                AstroPattern.大三角(unionPoints, starPosMap.getValue(set1.twoPoints.first()).sign.element)
              }
          }.filterNotNull().toSet()
        } ?: emptySet()
    }
  } // 大三角

  object Kite : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {
      return GrandTrine.getPattern(starPosMap)
        .map { it as AstroPattern.大三角 }
        .flatMap { grandTrine ->
          grandTrine.points.map { tail ->
            tail to modernCalculator {
              aspects = setOf(Aspect.OPPOSITION)
            }.getPointAspect(tail, starPosMap)
          }.filter { (_, oppoMap) ->
            oppoMap.isNotEmpty()
          }.map { (tail, oppoMap) ->
            tail to oppoMap.keys
          }.flatMap { (tail, heads: Set<Point>) ->
            heads.map { head ->
              AstroPattern.風箏(head, grandTrine.points.minus(tail), tail)
            }
          }
        }.toSet()
    }
  } // 風箏

  object TSquared : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {

      val aspectDataSet = highCalculator.getAspectDataSet(starPosMap, Planet.list)

      return aspectDataSet
        .filter { it.aspect == Aspect.OPPOSITION }
        .map { it.twoPoints }
        .map { twoPoints: Set<Point> ->
          val (p1, p2) = twoPoints.toList().let { it[0] to it[1] }
          val p1SquaredPoints = squareCalculator.getPointAspect(p1, starPosMap).keys
          val p2SquaredPoints = squareCalculator.getPointAspect(p2, starPosMap).keys
          twoPoints to p1SquaredPoints.intersect(p2SquaredPoints)
        }.filter { (_, commonSquaredPoints) ->
          commonSquaredPoints.isNotEmpty()
        }.flatMap { (two, squared) ->
          squared.map { squareP ->
            AstroPattern.三刑會沖(two, squareP)
          }
        }.toSet()
    }
  } // 三刑會沖

  object TSquareWithHelp : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {

      return TSquared.getPattern(starPosMap)
        .map { it as AstroPattern.三刑會沖 }
        .flatMap {
          sextileTrineCalculator.getPointAspect(it.squaredPoint, starPosMap).map { (k, v) ->
            AstroPattern.三刑會衝逢解(it, k)
          }
        }.toSet()
    }
  }

  object FingerOfGod : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {

      val aspectDataSet = mediumHighCalculator.getAspectDataSet(starPosMap, Planet.list)

      return aspectDataSet.filter {
        it.aspect == Aspect.QUINCUNX
      }.takeIf { it.size >= 2 } // 至少要兩個 QUINCUNX
        ?.toSet()?.let { dataSet ->
          Sets.combinations(dataSet, 2).asSequence().map { twoSets ->
            val (set1, set2) = twoSets.toList().let { it[0] to it[1] }
            val intersectedPoint = set1.twoPoints.intersect(set2.twoPoints)
            intersectedPoint to twoSets
          }.filter { it.first.isNotEmpty() }
            .map { (intersected, set) -> intersected.iterator().next() to set }
            .map { (point, twoSets: Set<HoroscopeAspectData>) ->
              val (set1, set2) = twoSets.toList().let { it[0] to it[1] }
              val other1 = set1.getAnotherPoint(point)!!
              val other2 = set2.getAnotherPoint(point)!!

              point to (other1 to other2)
            }.filter { (_, others) ->
              val (other1, other2) = others
              // 確保 other1 / other2 呈現 60度
              aspectEffective.isEffective(
                other1, starPosMap.getValue(other1).lng,
                other2, starPosMap.getValue(other2).lng, Aspect.SEXTILE)
            }.map { (point, others) ->
              AstroPattern.上帝之指(others.toList().toSet(), point)
            }.toList()
        }
        ?.toList()?.toSet() ?: emptySet()
    }
  }

  object GrandCross : IPatternFactory {
    override fun getPattern(starPosMap: Map<Point, IPos>): Set<AstroPattern> {

      return TSquared.getPattern(starPosMap)
        .takeIf { it.size >= 2 }
        ?.let { dataSets -> dataSets }
        ?.let { dataSets: Set<AstroPattern> ->
          //looser.invoke(dataSets , starPosMap)
          tighter.invoke(dataSets, starPosMap)
        } ?: emptySet()
    }

    /**
     * 比較寬鬆的比對法
     * 所有的 [AstroPattern.三刑會沖] , 找出 頂點(squaredPoint) , 比對此頂點是否有對沖點
     *  */
    private val looser = { dataSets: Set<AstroPattern>, starPosMap: Map<Point, IPos> ->
      val oppoCalculator = modernCalculator {
        aspects = setOf(Aspect.OPPOSITION)
      }
      dataSets.map { it as AstroPattern.三刑會沖 }
        .flatMap { tSquared ->
          oppoCalculator.getPointAspect(tSquared.squaredPoint, starPosMap).keys.map { oppo ->
            val quality = starPosMap.getValue(tSquared.squaredPoint).sign.quality
            AstroPattern.大十字(tSquared.oppoPoints.plus(tSquared.squaredPoint).plus(oppo), quality)
          }
        }.toSet()
    }

    /**
     * 比較嚴格的比對法
     * 把 [AstroPattern.三刑會沖] 兩兩抓出來比對， 全部必須只能四顆星，且彼此相刑
     * */
    private val tighter: (Set<AstroPattern>, starPosMap: Map<Point, IPos>) -> Set<AstroPattern.大十字> = { dataSets: Set<AstroPattern>, starPosMap: Map<Point, IPos> ->
      Sets.combinations(dataSets, 2).mapNotNull { twoSets ->
        val (set1, set2) = twoSets.toList().let { it[0] as AstroPattern.三刑會沖 to it[1] as AstroPattern.三刑會沖 }
        val unionPoints: Set<Point> = set1.oppoPoints.union(set2.oppoPoints)

        twoSets
          .takeIf { unionPoints.size == 4 }
          ?.takeIf {

            val (p1, p2) = set1.oppoPoints.toList().let { it[0] to it[1] }
            val (p3, p4) = set2.oppoPoints.toList().let { it[0] to it[1] }


            // 這樣比較「緊緻」
            aspectEffective.isEffective(p1, p3, starPosMap, Aspect.SQUARE)
              &&
              aspectEffective.isEffective(p1, p4, starPosMap, Aspect.SQUARE)
              &&
              aspectEffective.isEffective(p2, p3, starPosMap, Aspect.SQUARE)
              &&
              aspectEffective.isEffective(p2, p4, starPosMap, Aspect.SQUARE)
          }
          ?.let {
            val quality = starPosMap.getValue(set1.squaredPoint).sign.quality
            AstroPattern.大十字(unionPoints, quality)
          }
      }.toSet()
    }
  } // 大十字
}

