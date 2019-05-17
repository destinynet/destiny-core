/**
 * Created by smallufo on 2019-05-17.
 */
package destiny.astrology

import com.google.common.collect.Sets
import java.io.Serializable

class PatternContext(val aspectEffective: IAspectEffective,
                     val aspectsCalculator: IHoroscopeAspectsCalculator) : Serializable {

  private val horoAspectsCalculator = HoroscopeAspectsCalculator(aspectsCalculator)

  val grandTrine = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return horoAspectsCalculator.getAspectDataSet(starPosMap, Planet.list, aspects = setOf(Aspect.TRINE))
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->

          return Sets.combinations(dataSet, 3).mapNotNull { threeSets ->
            val (set1, set2, set3) = threeSets.toList().let { Triple(it[0], it[1], it[2]) }
            set1.points.union(set2.points).union(set3.points)
              .takeIf { unionPoints -> unionPoints.size == 3 }
              ?.takeIf { unionPoints ->
                Sets.combinations(unionPoints, 2).all { twoPoint ->
                  val (p1, p2) = twoPoint.toList().let { it[0] to it[1] }
                  aspectEffective.isEffective(p1, starPosMap.getValue(p1).lng, p2, starPosMap.getValue(p2).lng, Aspect.TRINE)
                }
              }
              ?.let { unionPoints ->
                AstroPattern.大三角(unionPoints, starPosMap.getValue(set1.points.first()).sign.element)
              }
          }.toSet()
        } ?: emptySet()
    }
  } // 大三角


  val kite = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return grandTrine.getPatterns(starPosMap, cuspDegreeMap)
        .map { it as AstroPattern.大三角 }
        .flatMap { grandTrine ->
          grandTrine.points.map { tail ->
            tail to HoroscopeAspectsCalculatorModern().getPointAspect(tail, starPosMap, aspects = setOf(Aspect.OPPOSITION))
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


  val tSquared = object : IPatternFactory {
    val twoAspects = setOf(Aspect.OPPOSITION, Aspect.SQUARE) // 180 , 90

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return horoAspectsCalculator.getAspectDataSet(starPosMap, Planet.list, twoAspects)
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->
          Sets.combinations(dataSet, 3).filter { threeSet ->
            threeSet.flatMap { it.points }.toSet().size == 3
          }.filter { threeSet ->
            threeSet.filter { it.aspect == Aspect.OPPOSITION }.size == 1
              && threeSet.filter { it.aspect == Aspect.SQUARE }.size == 2
          }.map { threeSet ->
            val oppoPoints = threeSet.first { it.aspect == Aspect.OPPOSITION }.points
            val squared = threeSet.flatMap { it.points }.toSet().minus(oppoPoints).first()
            AstroPattern.三刑會沖(oppoPoints, squared)
          }
        }?.toSet() ?: emptySet()

    }
  } // 三刑會沖


  val fingerOfGod = object : IPatternFactory {

    val twoAspects = setOf(Aspect.QUINCUNX, Aspect.SEXTILE) // 150 , 60

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      val aspectDataSet = horoAspectsCalculator.getAspectDataSet(starPosMap, Planet.list, twoAspects)

      return aspectDataSet.filter {
        it.aspect == Aspect.QUINCUNX
      }.takeIf { it.size >= 2 } // 至少要兩個 QUINCUNX
        ?.toSet()?.let { dataSet ->
          Sets.combinations(dataSet, 2).asSequence().map { twoSets ->
            val (set1, set2) = twoSets.toList().let { it[0] to it[1] }
            val intersectedPoint = set1.points.intersect(set2.points)
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


  val goldenYod = object : IPatternFactory {
    val twoAspects = setOf(Aspect.BIQUINTILE, Aspect.QUINTILE) // 144 , 72

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return horoAspectsCalculator.getAspectDataSet(starPosMap, aspects = twoAspects)
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->
          Sets.combinations(dataSet, 3)
            .filter { threeSet ->
              threeSet.flatMap { it.points }.toSet().size == 3
            }
            .filter { threeSet ->
              threeSet.filter { it.aspect == Aspect.BIQUINTILE }.size == 2
                && threeSet.filter { it.aspect == Aspect.QUINTILE }.size == 1
            }
            .map { threeSet ->
              val bottoms = threeSet.first { it.aspect == Aspect.QUINTILE }.points
              val pointer = threeSet.flatMap { it.points }.toSet().minus(bottoms).first()
              val pointerSign = starPosMap.getValue(pointer).sign
              AstroPattern.黃金指(bottoms, pointer, pointerSign)
            }
        }?.toSet() ?: emptySet()
    }
  }


  val grandCross = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return tSquared.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.let { dataSets: Set<AstroPattern> ->
          /** 所有的 [AstroPattern.三刑會沖] , 找出 頂點(squaredPoint) , 比對此頂點是否有對沖點
           * 並且要求，對衝點，與三刑的兩角尖，也要相刑
           * 才能確保比較漂亮的 大十字
           * */
          dataSets.map { it as AstroPattern.三刑會沖 }
            .flatMap { tSquared ->
              aspectsCalculator.getPointAspect(tSquared.squaredPoint, starPosMap, aspects = setOf(Aspect.OPPOSITION)).keys.mapNotNull { oppo: Point ->

                // oppo Point 還必須與 三刑會沖 兩角尖 相刑 , 才能確保比較漂亮的 大十字
                aspectsCalculator.getPointAspect(oppo, starPosMap, tSquared.oppoPoints, setOf(Aspect.SQUARE))
                  .takeIf { it.size == 2 }
                  ?.let {
                    //val quality = starPosMap.getValue(tSquared.squaredPoint).sign.quality
                    // 仍然有可能，四顆星不在同一種 quality 星座內 , 必須取「最多」者
                    val union4 = tSquared.oppoPoints.union(setOf(tSquared.squaredPoint, oppo))
                    val quality = union4.map { p -> starPosMap.getValue(p).sign.quality to p }
                      .groupBy { (q, _) -> q }
                      .toSortedMap()
                      .lastKey()

                    AstroPattern.大十字(union4, quality)
                  }
              }
            }.toSet()

        } ?: emptySet()
    }
  } // 大十字


  val doubleT = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return tSquared.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.map { pattern -> pattern as AstroPattern.三刑會沖 }
        ?.let { dataSet: List<AstroPattern.三刑會沖> ->
          Sets.combinations(dataSet.toSet(), 2).filter { twoSets ->
            // 先確保 有六顆星
            twoSets.flatMap { it.points }.toSet().size == 6
          }.filter { twoSets ->
            // 確保兩組 T-Square 的頂點不同
            twoSets.flatMap { setOf(it.squaredPoint) }.size == 2
          }.filter { twoSets ->
            // 而且此兩個頂點，並未對沖 (否則形成 GrandCross) , 也未相刑 or 合
            val (p1, p2) = twoSets.flatMap { setOf(it.squaredPoint) }.let { it[0] to it[1] }
            !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.OPPOSITION)
              && !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.SQUARE)
              && !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.CONJUNCTION)
          }.map { twoSets: Set<AstroPattern.三刑會沖> ->
            AstroPattern.DoubleT(twoSets)
          }
        }?.toSet() ?: emptySet()
    }
  } // DoubleT


  // 六芒星
  val hexagon = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return grandTrine.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.map { pattern -> pattern as AstroPattern.大三角 }
        ?.let { dataSet ->
          Sets.combinations(dataSet.toSet(), 2).filter { twoSets ->
            // 先確保 有六顆星
            twoSets.flatMap { it.points }.toSet().size == 6
          }.filter { twoSets ->
            // 兩組大三角中，每顆星都能在另一組中找到對沖的星
            val (g1, g2) = twoSets.toList().let { it[0] to it[1] }
            g1.points.all { p ->
              aspectsCalculator.getPointAspect(p, starPosMap, g2.points, aspects = setOf(Aspect.OPPOSITION)).size == 1
            }
          }.map { twoSets: Set<AstroPattern.大三角> ->
            AstroPattern.六芒星(twoSets)
          }
        }?.toSet() ?: emptySet()
    }
  } // 六芒星


  // 直角三角形
  val wedge = object : IPatternFactory {
    // 只比對 180 , 60 , 120 三種度數
    private val threeAspects = setOf(Aspect.OPPOSITION, Aspect.SEXTILE, Aspect.TRINE)

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      val dataSet: Set<HoroscopeAspectData> = horoAspectsCalculator.getAspectDataSet(starPosMap, aspects = threeAspects)

      return dataSet.takeIf { it.size >= 3 }
        ?.let {
          Sets.combinations(dataSet, 3).filter { threeSets ->
            // 確保三種交角都有
            threeSets.map { dataSet -> dataSet.aspect }.containsAll(threeAspects)
          }.filter { threeSets ->
            // 總共只有三顆星介入
            threeSets.flatMap { it.points }.toSet().size == 3
          }.map { threeSets ->
            val oppoPoints = threeSets.first { it.aspect == Aspect.OPPOSITION }.points
            val moderator = threeSets.flatMap { it.points }.toSet().minus(oppoPoints).iterator().next()
            AstroPattern.楔子(oppoPoints, moderator)
          }.toSet()
        } ?: emptySet()
    }
  } // 對衝，逢 Trine / Sextile , 形成 直角三角形

  // 五芒星 , 144 , 72
  val pentagram = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return goldenYod.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { patterns -> patterns.size >= 5 }
        ?.let { patterns ->
          Sets.combinations(patterns, 5)
            .filter { fiveSet -> fiveSet.flatMap { it.points }.toSet().size == 5 }
            .map { fiveSet ->
              val fivePoints = fiveSet.flatMap { it.points }.toSet()
              AstroPattern.五芒星(fivePoints)
            }
        }?.toSet() ?: emptySet()
    }
  } // 五芒星 (尚未出現範例)

  // 群星聚集 某星座
  val stelliumSign = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return starPosMap.entries.groupBy { (_, pos) -> pos.sign }
        .filter { (_ , list) -> list.size >= 4 }
        .map { (sign , list: List<Map.Entry<Point, IPos>>) ->
          val points = list.map { it.key }.toSet()
          AstroPattern.聚集星座(points , sign)
        }.toSet()
    }
  }

  // 群星聚集 某宮位
  val stelliumHouse = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return starPosMap.map { (point , pos) -> point to IHoroscopeModel.getHouse(pos.lng , cuspDegreeMap) }
        .groupBy { (_ , house) -> house}
        .filter { (_ , list: List<Pair<Point, Int>>) -> list.size >= 4 }
        .map { (house , list: List<Pair<Point, Int>>) ->
          val points = list.map { it.first }.toSet()
          AstroPattern.聚集宮位(points , house)
        }.toSet()
    }
  }

}