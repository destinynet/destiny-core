/**
 * Created by smallufo on 2019-05-17.
 */
package destiny.astrology

import com.google.common.collect.Sets
import mu.KotlinLogging
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
                val score = threeSets.takeIf { sets -> sets.all { it.score != null } }
                  ?.map { it.score!! }?.average()
                AstroPattern.大三角(unionPoints, starPosMap.getValue(set1.points.first()).sign.element, score)
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
          // 大三角的 每個點 , 都當作風箏的尾巴，去找是否有對沖的點 (亦即：風箏頭)
          grandTrine.points.map { tail ->
            tail to HoroscopeAspectsCalculatorModern().getPointAspectAndScore(tail, starPosMap, aspects = setOf(Aspect.OPPOSITION))
          }.filter { (_, oppoMap) ->
            oppoMap.isNotEmpty()
          }.flatMap { (tail, oppoMap: Map<Point, Pair<Aspect, Double>>) ->
            oppoMap.map { (head, aspectAndScore) ->
              Triple(tail, head, aspectAndScore.second)
            }.mapNotNull { (tail, head, oppoScore) ->
              // 每個 head 都需要與 兩翼 SEXTILE
              grandTrine.points.minus(tail).map { wingPoint ->
                wingPoint to aspectEffective.isEffectiveAndScore(head, wingPoint, starPosMap, Aspect.SEXTILE)
              }.takeIf { pairs ->
                pairs.all { it.second.first }
              }?.map { (wing, booleanAndScore) ->
                wing to booleanAndScore.second
              }?.toMap()
                ?.let { map: Map<Point, Double> ->
                  val wings = map.keys
                  /** 分數 : [AstroPattern.大三角] + 對沖分數 +  head與兩個翅膀 [Aspect.SEXTILE] 的分數 , 四者平均 */
                  val score = grandTrine.score?.let { setOf(it) }?.plus(oppoScore)?.plus(map.values)?.average()
                  AstroPattern.風箏(head, wings, tail, score)
                }
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
            val score: Double? = threeSet.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!! }?.average()
            val oppoPoints = threeSet.first { it.aspect == Aspect.OPPOSITION }.points
            val squared = threeSet.flatMap { it.points }.toSet().minus(oppoPoints).first()
            AstroPattern.三刑會沖(oppoPoints, squared, score)
          }
        }?.toSet() ?: emptySet()

    }
  } // 三刑會沖


  val fingerOfGod = object : IPatternFactory {

    val twoAspects = setOf(Aspect.QUINCUNX, Aspect.SEXTILE) // 150 , 60

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {


      return horoAspectsCalculator.getAspectDataSet(starPosMap, Planet.list, setOf(Aspect.QUINCUNX))
        .takeIf { it.size >= 2 }
        ?.let { dataSet ->
          // 任兩個 QUINCUNX ,
          Sets.combinations(dataSet, 2)
            .filter { twoSets: Set<HoroscopeAspectData> ->
              // 確保組合而成的 points 若共有三顆星
              twoSets.flatMap { it.points }.toSet().size == 3
            }.map { twoSets: Set<HoroscopeAspectData> ->
              val (set1: HoroscopeAspectData, set2: HoroscopeAspectData) = twoSets.toList().let { it[0] to it[1] }
              val intersectedPoint = set1.points.intersect(set2.points)
              val (other1: Point, other2: Point) = twoSets.flatMap { it.points }.toSet().minus(intersectedPoint).toList().let { it[0] to it[1] }
              // 確保 另外兩點 形成 60 度
              val (sextile, sextileScore) = aspectEffective.isEffectiveAndScore(other1, other2, starPosMap, Aspect.SEXTILE)
              twoSets to (sextile to sextileScore)
            }.filter { (_, sextileAndScore) -> sextileAndScore.first }
            .map { (twoSets, sextileScore) ->
              val score: Double? = twoSets.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!! }?.plus(sextileScore.second)?.average()
              val (set1: HoroscopeAspectData, set2: HoroscopeAspectData) = twoSets.toList().let { it[0] to it[1] }
              val pointer = set1.points.intersect(set2.points).first()
              val pointerSign = starPosMap.getValue(pointer).sign
              val bottoms: Set<Point> = twoSets.flatMap { it.points }.toSet().minus(pointer)
              AstroPattern.上帝之指(bottoms, pointer, pointerSign, score)
            }
        }?.toSet() ?: emptySet()
    }
  }


  val goldenYod = object : IPatternFactory {

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return horoAspectsCalculator.getAspectDataSet(starPosMap, aspects = setOf(Aspect.BIQUINTILE, Aspect.QUINTILE))  // 144 , 72
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->
          Sets.combinations(dataSet, 3)
            .filter { threePairs ->
              threePairs.flatMap { it.points }.toSet().size == 3
            }
            .filter { threePairs ->
              threePairs.filter { it.aspect == Aspect.BIQUINTILE }.size == 2
                && threePairs.filter { it.aspect == Aspect.QUINTILE }.size == 1
            }
            .map { threePairs: Set<HoroscopeAspectData> ->

              val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!! }?.average()

              val bottoms = threePairs.first { it.aspect == Aspect.QUINTILE }.points
              val pointer = threePairs.flatMap { it.points }.toSet().minus(bottoms).first()
              val pointerSign = starPosMap.getValue(pointer).sign
              AstroPattern.黃金指(bottoms, pointer, pointerSign, score)
            }
        }?.toSet() ?: emptySet()
    }
  }


  val grandCross = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return tSquared.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.let { dataSets: Set<AstroPattern> ->
          /** 所有的 [AstroPattern.三刑會沖] , 找出 頂點( [AstroPattern.三刑會沖.squaredPoint]) , 比對此頂點是否有對沖點
           * 並且要求，對衝點，與三刑的兩角尖，也要相刑
           * 才能確保比較漂亮的 大十字
           * */
          dataSets.map { it as AstroPattern.三刑會沖 }
            .flatMap { tSquared ->
              aspectsCalculator.getPointAspect(tSquared.squaredPoint, starPosMap, aspects = setOf(Aspect.OPPOSITION)).keys.mapNotNull { oppo: Point ->

                // oppo Point 還必須與 三刑會沖 兩角尖 相刑 , 才能確保比較漂亮的 大十字
                aspectsCalculator.getPointAspectAndScore(oppo, starPosMap, tSquared.oppoPoints, setOf(Aspect.SQUARE))
                  .takeIf { it.size == 2 }
                  ?.let { twoSquared ->
                    // 一個 T-Squared 的分數
                    val tSquaredScore = tSquared.score
                    // 加上其對衝點 , 與此 T-Squared 兩底點的 squared 分數
                    val twinSquaredScore: List<Double> = twoSquared.map { it.value.second }
                    // 三個值 , 平均
                    val score: Double? = tSquaredScore?.let { twinSquaredScore.plus(it).average() }

                    // 仍然有可能，四顆星不在同一種 quality 星座內 , 必須取「最多」者
                    val union4 = tSquared.oppoPoints.union(setOf(tSquared.squaredPoint, oppo))
                    val quality = union4.map { p -> starPosMap.getValue(p).sign.quality to p }
                      .groupBy { (q, _) -> q }
                      .toSortedMap()
                      .lastKey()

                    AstroPattern.大十字(union4, quality, score)
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
          Sets.combinations(dataSet.toSet(), 2).filter { twoPatterns ->
            // 先確保 有六顆星
            twoPatterns.flatMap { it.points }.toSet().size == 6
          }.filter { twoPatterns ->
            // 確保兩組 T-Square 的頂點不同
            twoPatterns.flatMap { setOf(it.squaredPoint) }.size == 2
          }.filter { twoPatterns ->
            // 而且此兩個頂點，並未對沖 (否則形成 GrandCross) , 也未相刑 or 合
            val (p1, p2) = twoPatterns.flatMap { setOf(it.squaredPoint) }.let { it[0] to it[1] }
            !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.OPPOSITION)
              && !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.SQUARE)
              && !aspectEffective.isEffective(p1, p2, starPosMap, Aspect.CONJUNCTION)
          }.map { twoPatterns: Set<AstroPattern.三刑會沖> ->
            val score: Double? = twoPatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!! }?.average()
            AstroPattern.DoubleT(twoPatterns, score)
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
          }.filter { twoTrines ->
            // 兩組大三角中，每顆星都能在另一組中找到對沖的星
            val (g1, g2) = twoTrines.toList().let { it[0] to it[1] }
            g1.points.all { p ->
              aspectsCalculator.getPointAspect(p, starPosMap, g2.points, aspects = setOf(Aspect.OPPOSITION)).size == 1
            }
          }.map { twoTrines: Set<AstroPattern.大三角> ->

            val score: Double? = twoTrines.takeIf { trines -> trines.all { it.score != null } }?.map { it.score!! }?.average()

            AstroPattern.六芒星(twoTrines, score)
          }
        }?.toSet() ?: emptySet()
    }
  } // 六芒星


  // 180 沖 , 逢 第三顆星 , 以 60/120 介入，緩和局勢
  val wedge = object : IPatternFactory {
    // 只比對 180 , 60 , 120 三種度數
    private val threeAspects = setOf(Aspect.OPPOSITION, Aspect.SEXTILE, Aspect.TRINE)

    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      val dataSet: Set<HoroscopeAspectData> = horoAspectsCalculator.getAspectDataSet(starPosMap, aspects = threeAspects)

      return dataSet.takeIf { it.size >= 3 }
        ?.let {
          Sets.combinations(dataSet, 3).filter { threePairs ->
            // 確保三種交角都有
            threePairs.map { dataSet -> dataSet.aspect }.containsAll(threeAspects)
          }.filter { threePairs ->
            // 總共只有三顆星介入
            threePairs.flatMap { it.points }.toSet().size == 3
          }.map { threePairs ->
            val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!! }?.average()

            val oppoPoints = threePairs.first { it.aspect == Aspect.OPPOSITION }.points
            val moderator = threePairs.flatMap { it.points }.toSet().minus(oppoPoints).iterator().next()
            val moderatorSign = starPosMap.getValue(moderator).sign
            AstroPattern.楔子(oppoPoints, moderator, moderatorSign, score)
          }.toSet()
        } ?: emptySet()
    }
  } // 對衝，逢 Trine / Sextile , 形成 直角三角形


  /** [AstroPattern.神秘長方形] */
  val mysticRectangle = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return wedge.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { it.size >= 2 } // 確保至少兩組 wedge
        ?.map { it as AstroPattern.楔子 }
        ?.let { patterns ->
          Sets.combinations(patterns.toSet() , 2).asSequence().map { twoWedges ->
            val (wedge1 , wedge2) = twoWedges.toList().let { it[0] to it[1] }
            // 兩組 wedge 的 moderator 又互相對沖
            val (moderatorOppo , oppoScore) = aspectEffective.isEffectiveAndScore(wedge1.moderator , wedge2.moderator , starPosMap , Aspect.OPPOSITION)

            val unionPoints = twoWedges.flatMap { it.points }.toSet()

            // 兩組 wedges 只能有四顆星
            val matched: Boolean = moderatorOppo && unionPoints.size == 4
            matched to Pair(oppoScore , twoWedges)
          }.filter { (moderatorOppo , _) -> moderatorOppo }
            .map { (_ , pair: Pair<Double, Set<AstroPattern.楔子>>) -> pair }
            .map { (oppoScore , twoWedges) ->
              val unionPoints = twoWedges.flatMap { it.points }.toSet()
              // 分數 : 以兩組 wedge 個別分數 , 加上 moderator 對沖分數 , 三者平均
              val score: Double? = twoWedges.takeIf { pattern -> pattern.all { it.score!= null } }?.map { it.score!! }?.plus(oppoScore)?.average()
              AstroPattern.神秘長方形(unionPoints, score)
            }.toSet()
        }?: emptySet()
    }
  } // 神秘長方形


  // 五芒星 , 144 , 72
  val pentagram = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return goldenYod.getPatterns(starPosMap, cuspDegreeMap)
        .takeIf { patterns -> patterns.size >= 5 }
        ?.let { patterns ->
          Sets.combinations(patterns, 5)
            .filter { fiveSet -> fiveSet.flatMap { it.points }.toSet().size == 5 }
            .map { fivePatterns ->
              val score: Double? = fivePatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!! }?.average()
              val fivePoints = fivePatterns.flatMap { it.points }.toSet()
              AstroPattern.五芒星(fivePoints, score)
            }
        }?.toSet() ?: emptySet()
    }
  } // 五芒星 (尚未出現範例)

  // 群星聚集 某星座
  val stelliumSign = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return starPosMap.entries.groupBy { (_, pos) -> pos.sign }
        .filter { (_, list) -> list.size >= 4 }
        .map { (sign, list: List<Map.Entry<Point, IPos>>) ->
          val points = list.map { it.key }.toSet()
          /** 分數算法： 以該星座內 [Aspect.CONJUNCTION] 分數平均 */
          val score = Sets.combinations(points , 2).map { pair ->
            val (p1 , p2) = pair.toList().let { it[0] to it[1] }
            aspectEffective.isEffectiveAndScore(p1 , p2 , starPosMap , Aspect.CONJUNCTION)
          }
            //.filter { (effective , _) -> effective } 對於「同一星座內，但是沒有形成合相的雙星」其分數雖然是零分，但是不要過濾
            .map { (_ , score) -> score }
            .average()

          AstroPattern.聚集星座(points, sign , score)
        }.toSet()
    }
  }

  // 群星聚集 某宮位
  val stelliumHouse = object : IPatternFactory {
    override fun getPatterns(starPosMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return starPosMap.map { (point, pos) -> point to IHoroscopeModel.getHouse(pos.lng, cuspDegreeMap) }
        .groupBy { (_, house) -> house }
        .filter { (_, list: List<Pair<Point, Int>>) -> list.size >= 4 }
        .map { (house, list: List<Pair<Point, Int>>) ->
          val points = list.map { it.first }.toSet()
          /** 分數算法： 以該宮位內 [Aspect.CONJUNCTION] 分數平均 */
          val score = Sets.combinations(points , 2).map { pair ->
            val (p1 , p2) = pair.toList().let { it[0] to it[1] }
            aspectEffective.isEffectiveAndScore(p1 , p2 , starPosMap , Aspect.CONJUNCTION)
          }.map { (_, score) -> score }
            .average()
          AstroPattern.聚集宮位(points, house , score)
        }.toSet()
    }
  }

  companion object {
    val logger = KotlinLogging.logger {  }
  }
}