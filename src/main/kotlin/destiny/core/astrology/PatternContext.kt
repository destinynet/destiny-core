/**
 * Created by smallufo on 2019-05-17.
 */
package destiny.core.astrology

import com.google.common.collect.Sets
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.KotlinLogging
import destiny.tools.Score
import destiny.tools.Score.Companion.average
import destiny.tools.Score.Companion.toScore
import org.apache.commons.math3.ml.clustering.Cluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.DBSCANClusterer
import java.io.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class PatternContext(val aspectEffective: IAspectEffective,
                     val aspectCalculator: IAspectCalculator) : Serializable {

  private fun AstroPoint.signHouse(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): PointSignHouse {
    return posMap.getValue(this).let { iPos ->
      val sign: ZodiacSign = iPos.sign
      val house: Int = IHoroscopeModel.getHouse(iPos.lngDeg, cuspDegreeMap)
      PointSignHouse(this, sign, house)
    }
  }

  val grandTrine = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return aspectCalculator.getAspectPatterns(posMap, aspects = setOf(TRINE))
        .takeIf { it.size >= 3 }
        ?.let { patterns ->

          return Sets.combinations(patterns, 3).asSequence().mapNotNull { threeSets ->
            val (set1, set2, set3) = threeSets.toList().let { Triple(it[0], it[1], it[2]) }
            set1.points.union(set2.points).union(set3.points)
              .takeIf { unionPoints -> unionPoints.size == 3 }
              ?.takeIf { unionPoints ->
                Sets.combinations(unionPoints, 2).all { twoPoint ->
                  val (p1, p2) = twoPoint.toList().let { it[0] to it[1] }
                  aspectEffective.isEffective(p1, posMap.getValue(p1).lngDeg, p2, posMap.getValue(p2).lngDeg, TRINE)
                }
              }
              ?.let { unionPoints ->
                val score = threeSets.takeIf { sets -> sets.all { it.score != null } }
                  ?.map { it.score!!.value }?.average()
                AstroPattern.GrandTrine(unionPoints, posMap.getValue(set1.points.first()).sign.element, score?.toScore())
              }
          }.toSet()
        } ?: emptySet()
    }
  } // 大三角


  private val kite = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return grandTrine.getPatterns(posMap, cuspDegreeMap)
        .map { it as AstroPattern.GrandTrine }
        .flatMap { grandTrine ->
          // 大三角的 每個點 , 都當作風箏的尾巴，去找是否有對沖的點 (亦即：風箏頭)
          grandTrine.points.map { tail ->
            tail to aspectCalculator.getPointAspectAndScore(tail, posMap, aspects = setOf(OPPOSITION))
          }.filter { (_, oppoSet) ->
            oppoSet.isNotEmpty()
          }.flatMap { (tail, oppoSet: Set<Triple<AstroPoint, Aspect, Score>>) ->
            oppoSet.map { (head, _, oppoScore) ->
              Triple(tail, head, oppoScore)
            }.mapNotNull { (tail, head, oppoScore) ->
              // 每個 head 都需要與 兩翼 SEXTILE

              grandTrine.points.minus(tail).map { wingPoint ->
                wingPoint to aspectEffective.getEffectiveErrorAndScore(head, wingPoint, posMap, SEXTILE)
              }.takeIf { list ->
                list.all { (_, maybeErrorAndScore) -> maybeErrorAndScore != null }
              }?.associate { (wing, maybeErrorAndScore) -> wing to maybeErrorAndScore!!.second }
                ?.let { map: Map<AstroPoint, Score> ->
                  val wings = map.keys
                  /** 分數 : [AstroPattern.GrandTrine] + 對沖分數 +  head與兩個翅膀 [Aspect.SEXTILE] 的分數 , 四者平均 */
                  val score = grandTrine.score?.let { setOf(it) }?.plus(oppoScore)?.plus(map.values)?.average()
                  AstroPattern.Kite(head.signHouse(posMap, cuspDegreeMap), wings, tail.signHouse(posMap, cuspDegreeMap), score)
                }
            }
          }
        }.toSet()
    }
  } // 風箏


  val tSquared = object : IPatternFactory {
    val twoAspects = setOf(OPPOSITION, SQUARE) // 180 , 90

    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {

      return aspectCalculator.getAspectPatterns(posMap, aspects = twoAspects)
        .takeIf { it.size >= 3 }
        ?.let { pairs ->
          Sets.combinations(pairs, 3).asSequence().filter { threeSet ->
            threeSet.flatMap { it.points }.toSet().size == 3
          }.filter { threeSet ->
            threeSet.filter { it.aspect == OPPOSITION }.size == 1
              && threeSet.filter { it.aspect == SQUARE }.size == 2
          }.map { threeSet ->
            val score: Double? = threeSet.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!!.value }?.average()
            val oppoPoints: List<AstroPoint> = threeSet.first { it.aspect == OPPOSITION }.points
            val squared = threeSet.flatMap { it.points }.toSet().minus(oppoPoints.toSet()).first().signHouse(posMap, cuspDegreeMap)
            AstroPattern.TSquared(oppoPoints.toSet(), squared, score?.toScore())
          }
        }?.toSet() ?: emptySet()

    }
  } // 三刑會沖


  // 上帝之指
  val yod = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return aspectCalculator.getAspectPatterns(posMap, aspects = setOf(QUINCUNX))
        .takeIf { it.size >= 2 }
        ?.let { patterns ->
          // 任兩個 QUINCUNX ,
          Sets.combinations(patterns, 2).asSequence().filter { twoSets: Set<IPointAspectPattern> ->
            // 確保組合而成的 points 若共有三顆星
            twoSets.flatMap { it.points }.toSet().size == 3
          }.map { twoSets: Set<IPointAspectPattern> ->
            val (set1: IPointAspectPattern, set2: IPointAspectPattern) = twoSets.toList().let { it[0] to it[1] }
            val intersectedPoint = set1.points.intersect(set2.points.toSet())
            val (other1: AstroPoint, other2: AstroPoint) = twoSets.flatMap { it.points }.toSet().minus(intersectedPoint).toList().let { it[0] to it[1] }
            // 確保 另外兩點 形成 60 度
            twoSets to aspectEffective.getEffectiveErrorAndScore(other1, other2, posMap, SEXTILE)
          }
            .filter { (_ , maybeErrorAndScore) -> maybeErrorAndScore != null}
            .map { (twoSets , errorAndScore) -> twoSets to errorAndScore!! }
            .map { (twoSets, errorAndScore) ->
              val score: Score? = twoSets.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!! }?.plus(errorAndScore.second)?.average()
              val (set1: IPointAspectPattern, set2: IPointAspectPattern) = twoSets.toList().let { it[0] to it[1] }
              val pointer = set1.points.intersect(set2.points.toSet()).first().signHouse(posMap, cuspDegreeMap)
              val bottoms: Set<AstroPoint> = twoSets.flatMap { it.points }.toSet().minus(pointer.point)
              AstroPattern.Yod(bottoms, pointer, score)
            }
        }?.toSet() ?: emptySet()
    }
  } // 上帝之指

  // 回力鏢 : YOD + 對沖點
  private val boomerang = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return yod.getPatterns(posMap, cuspDegreeMap)
        .map { it as AstroPattern.Yod }
        .flatMap { pattern ->
          aspectCalculator.getPointAspectAndScore(pattern.apex.point, posMap, posMap.keys, setOf(OPPOSITION))
            .map { (oppoPoint, _, oppoScore) ->

              // 對沖點，還必須與兩翼形成30度
              val validScores: Set<Score> = pattern.bottoms.mapNotNull { bottom ->
                aspectEffective.getEffectiveErrorAndScore(oppoPoint, bottom, posMap, SEMISEXTILE)?.second
              }.toSet()

              Triple(oppoPoint, oppoScore, validScores)
            }
            .filter { (_, _, validScores) -> validScores.size == 2 }
            .map { (oppoPoint, oppoScore, _) ->
              // 分數 : 上帝之指分數 + 對沖分數 + 兩個60度的分數 , 平均
              //val score: Double? = pattern.score?.let { patternScore -> bottoms60ScoreMap.map { sextileScore -> sextileScore.value }.plus(oppoScore).plus(patternScore).average() }
              // 分數 : 上帝之指分數 + 對沖分數 平均
              val score = pattern.score?.let { patternScore -> setOf(oppoScore).plus(patternScore).average() }
              AstroPattern.Boomerang(pattern, oppoPoint.signHouse(posMap, cuspDegreeMap), score)
            }
        }.toSet()
    }
  } // 回力鏢 : YOD + 對沖點


  val goldenYod = object : IPatternFactory {

    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return aspectCalculator.getAspectPatterns(posMap, aspects = setOf(BIQUINTILE, QUINTILE))  // 144 , 72
        .takeIf { it.size >= 3 }
        ?.let { patterns ->
          Sets.combinations(patterns, 3).asSequence().filter { threePairs ->
            threePairs.flatMap { it.points }.toSet().size == 3
          }
            .filter { threePairs ->
              threePairs.filter { it.aspect == BIQUINTILE }.size == 2
                && threePairs.filter { it.aspect == QUINTILE }.size == 1
            }
            .map { threePairs: Set<IPointAspectPattern> ->

              val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!!.value }?.average()

              // caution : list.toSet() !
              val bottoms = threePairs.first { it.aspect == QUINTILE }.points.toSet()
              val pointer = threePairs.flatMap { it.points }.toSet().minus(bottoms.toSet()).first()

              AstroPattern.GoldenYod(bottoms, pointer.signHouse(posMap, cuspDegreeMap), score?.toScore())
            }
        }?.toSet() ?: emptySet()
    }
  }


  private val grandCross = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {

      return tSquared.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.let { patterns: Set<AstroPattern> ->
          /** 所有的 [AstroPattern.TSquared] , 找出 頂點( [AstroPattern.TSquared.squared]) , 比對此頂點是否有對沖點
           * 並且要求，對衝點，與三刑的兩角尖，也要相刑
           * 才能確保比較漂亮的 大十字
           * */
          patterns.asSequence().map { it as AstroPattern.TSquared }
            .flatMap { tSquared ->
              aspectCalculator.getPointAspect(tSquared.squared.point, posMap, aspects = setOf(OPPOSITION)).keys.mapNotNull { oppo: AstroPoint ->

                // oppo Point 還必須與 三刑會沖 兩角尖 相刑 , 才能確保比較漂亮的 大十字

                aspectCalculator.getPointAspectAndScore(oppo, posMap, tSquared.oppoPoints, setOf(SQUARE))
                  .takeIf { it.size == 2 }
                  ?.let { twoSquared: Set<Triple<AstroPoint, Aspect, Score>> ->
                    // 一個 T-Squared 的分數
                    val tSquaredScore = tSquared.score
                    // 加上其對衝點 , 與此 T-Squared 兩底點的 squared 分數
                    val twinSquaredScore: List<Score> = twoSquared.map { it.third }
                    // 三個值 , 平均
                    val score = tSquaredScore?.let { twinSquaredScore.plus(it).average() }

                    // 仍然有可能，四顆星不在同一種 quality 星座內 , 必須取「最多」者
                    val union4 = tSquared.oppoPoints.union(setOf(tSquared.squared.point, oppo))
                    val quality = union4.map { p -> posMap.getValue(p).sign.quality to p }
                      .groupBy { (q, _) -> q }
                      .toSortedMap()
                      .lastKey()

                    AstroPattern.GrandCross(union4, quality, score)
                  }
              }.asSequence()
            }.toSet()

        } ?: emptySet()
    }
  } // 大十字


  private val doubleT = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return tSquared.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.asSequence()
        ?.map { pattern -> pattern as AstroPattern.TSquared }
        ?.let { patterns: Sequence<AstroPattern.TSquared> ->
          Sets.combinations(patterns.toSet(), 2).asSequence().filter { twoPatterns ->
            // 先確保 有六顆星
            twoPatterns.flatMap { it.points }.toSet().size == 6
          }.filter { twoPatterns ->
            // 確保兩組 T-Square 的頂點不同
            twoPatterns.flatMap { setOf(it.squared.point) }.size == 2
          }.filter { twoPatterns ->
            // 而且此兩個頂點，並未對沖 (否則形成 GrandCross) , 也未相刑 or 合
            val (p1, p2) = twoPatterns.flatMap { setOf(it.squared.point) }.let { it[0] to it[1] }
            !aspectEffective.isEffective(p1, p2, posMap, OPPOSITION)
              && !aspectEffective.isEffective(p1, p2, posMap, SQUARE)
              && !aspectEffective.isEffective(p1, p2, posMap, CONJUNCTION)
          }.map { twoPatterns: Set<AstroPattern.TSquared> ->
            val score = twoPatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!!.value }?.average()?.toScore()
            AstroPattern.DoubleT(twoPatterns, score)
          }
        }?.toSet() ?: emptySet()
    }
  } // DoubleT


  // 六芒星
  private val hexagon = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return grandTrine.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.asSequence()
        ?.map { pattern -> pattern as AstroPattern.GrandTrine }
        ?.let { patterns ->
          Sets.combinations(patterns.toSet(), 2).asSequence().filter { twoSets ->
            // 先確保 有六顆星
            twoSets.flatMap { it.points }.toSet().size == 6
          }.filter { twoTrines ->
            // 兩組大三角中，每顆星都能在另一組中找到對沖的星
            val (g1, g2) = twoTrines.toList().let { it[0] to it[1] }
            g1.points.all { p ->
              aspectCalculator.getPointAspect(p, posMap, g2.points, aspects = setOf(OPPOSITION)).size == 1
            }
          }.map { twoTrines: Set<AstroPattern.GrandTrine> ->
            val score = twoTrines.takeIf { trines -> trines.all { it.score != null } }?.map { it.score!!.value }?.average()?.toScore()
            AstroPattern.Hexagon(twoTrines, score)
          }
        }?.toSet() ?: emptySet()
    }
  } // 六芒星


  // 180 沖 , 逢 第三顆星 , 以 60/120 介入，緩和局勢
  val wedge = object : IPatternFactory {
    // 只比對 180 , 60 , 120 三種度數
    private val threeAspects = setOf(OPPOSITION, SEXTILE, TRINE)

    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {

      val patterns: Set<IPointAspectPattern> = aspectCalculator.getAspectPatterns(posMap, aspects = threeAspects)

      return patterns.takeIf { it.size >= 3 }
        ?.let {
          Sets.combinations(patterns, 3).asSequence().filter { threePairs ->
            // 確保三種交角都有
            threePairs.map { pattern -> pattern.aspect }.containsAll(threeAspects)
          }.filter { threePairs ->
            // 總共只有三顆星介入
            threePairs.flatMap { it.points }.toSet().size == 3
          }.map { threePairs ->
            val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!!.value }?.average()

            val oppoPoints = threePairs.first { it.aspect == OPPOSITION }.points
            val moderator = threePairs.flatMap { it.points }.toSet().minus(oppoPoints.toSet()).first()

            AstroPattern.Wedge(oppoPoints.toSet(), moderator.signHouse(posMap, cuspDegreeMap), score?.toScore())
          }.toSet()
        } ?: emptySet()
    }
  } // 對衝，逢 Trine / Sextile , 形成 直角三角形


  /** [AstroPattern.MysticRectangle] 神秘長方形 */
  private val mysticRectangle = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return wedge.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 } // 確保至少兩組 wedge
        ?.asSequence()
        ?.map { it as AstroPattern.Wedge }
        ?.let { patterns ->

          Sets.combinations(patterns.toSet(), 2).asSequence().map { twoWedges ->
            val (wedge1, wedge2) = twoWedges.toList().let { it[0] to it[1] }
            val unionPoints = twoWedges.flatMap { it.points }.toSet()

            // 兩組 wedges 只能有四顆星
            twoWedges to if (unionPoints.size == 4) {
              // 兩組 wedge 的 moderator 又互相對沖
              aspectEffective.getEffectiveErrorAndScore(wedge1.mediator.point, wedge2.mediator.point, posMap, OPPOSITION)?.second
            } else {
              null
            }
          }.filter { (_ , maybeScore) -> maybeScore != null }
            .map { (twoWedges , maybeScore) -> twoWedges to maybeScore!! }
            .map { (twoWedges , oppoScore) ->
              val (wedge1 , wedge2) = twoWedges.iterator().let { it.next() to it.next()}
              val oppoGroups = buildSet {
                addAll(twoWedges.map { it.oppoPoints }.toSet())
                add(setOf(wedge1.mediator.point , wedge2.mediator.point))
              }
              // 分數 : 以兩組 wedge 個別分數 , 加上 moderator 對沖分數 , 三者平均
              val score: Score? = twoWedges.takeIf { pattern -> pattern.all { it.score != null } }?.map { it.score!! }?.plus(oppoScore)?.average()
              AstroPattern.MysticRectangle(oppoGroups, score)
            }.toSet()
        } ?: emptySet()
    }
  } // 神秘長方形


  // 五芒星 , 144 , 72
  private val pentagram = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {

      return goldenYod.getPatterns(posMap, cuspDegreeMap)
        .takeIf { patterns -> patterns.size >= 5 }
        ?.let { patterns ->
          Sets.combinations(patterns, 5).asSequence()
            .filter { fiveSet -> fiveSet.flatMap { it.points }.toSet().size == 5 }
            .map { fivePatterns ->
              val score: Double? = fivePatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!!.value }?.average()
              val fivePoints = fivePatterns.flatMap { it.points }.toSet()
              AstroPattern.Pentagram(fivePoints, score?.toScore())
            }
        }?.toSet() ?: emptySet()
    }
  } // 五芒星 (尚未出現範例)

  // 群星聚集 某星座 (至少四顆星)
  private val stelliumSign = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return posMap.entries.groupBy { (_, pos) -> pos.sign }
        .filter { (_, list) -> list.size >= 4 }
        .map { (sign, list: List<Map.Entry<AstroPoint, IPos>>) ->
          val points = list.map { it.key }.toSet()
          /** 分數算法： 以該星座內 [Aspect.CONJUNCTION] 分數平均 */

          val score = Sets.combinations(points, 2).asSequence().map { pair ->
            val (p1, p2) = pair.toList().let { it[0] to it[1] }
            // 對於「同一星座內，但是沒有形成合相的雙星」其分數雖然是零分，但是不要過濾 , 就給 零分
            aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, CONJUNCTION)?.second ?: 0.0.toScore()
          }.toList().average()

          AstroPattern.StelliumSign(points, sign, score)
        }.toSet()
    }
  }

  // 群星聚集 某宮位 (至少四顆星)
  private val stelliumHouse = object : IPatternFactory {
    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      return posMap.asSequence().map { (point, pos) -> point to IHoroscopeModel.getHouse(pos.lngDeg, cuspDegreeMap) }
        .groupBy { (_, house) -> house }
        .filter { (_, list: List<Pair<AstroPoint, Int>>) -> list.size >= 4 }
        .map { (house, list: List<Pair<AstroPoint, Int>>) ->
          val points = list.map { it.first }.toSet()
          /** 分數算法： 以該宮位內 [Aspect.CONJUNCTION] 分數平均 */
          val score = Sets.combinations(points, 2).asSequence().map { pair ->
            val (p1, p2) = pair.toList().let { it[0] to it[1] }
            aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, CONJUNCTION)?.second ?: 0.0.toScore()
          }.toList().average()

          AstroPattern.StelliumHouse(points, house, score)
        }.toSet()
    }
  }

  inner class PointCluster(val point: AstroPoint, val lngDeg: Double) : Clusterable {
    override fun getPoint(): DoubleArray {
      return arrayOf(lngDeg).toDoubleArray()
    }
  }

  // 星群對峙 : 兩組 3顆星以上的合相星群 彼此對沖
  private val confrontation = object : IPatternFactory {

    override fun getPatterns(posMap: Map<AstroPoint, IPos>, cuspDegreeMap: Map<Int, ZodiacDegree>): Set<AstroPattern> {
      val pointMap = posMap.map { (point, pos) -> PointCluster(point, pos.lng) }
      val cluster = DBSCANClusterer<PointCluster>(6.0, 2) { arr1, arr2 -> ZodiacDegree.getAngle(arr1[0], arr2[0]) }

      return cluster.cluster(pointMap).let { list: List<Cluster<PointCluster>> ->
        list
          .takeIf { clusters -> clusters.size >= 2 }
          ?.let { clusters ->
            logger.trace("clusters size >=2 !! : {}", clusters.size)
            Sets.combinations(clusters.toSet(), 2).asSequence().map { twoClusters: Set<Cluster<PointCluster>> ->
              val (cluster1: Cluster<PointCluster>, cluster2: Cluster<PointCluster>) = twoClusters.toList().let { it[0] to it[1] }
              val group1 = cluster1.points.map { it.point }
              val group2 = cluster2.points.map { it.point }

              logger.trace { "group1 = $group1 , group2 = $group2" }
              // 兩個群組的中點是否對沖
              val mid1 = calculateAverageAngle(cluster1.points.map { it.lngDeg }).toZodiacDegree()
              val mid2 = calculateAverageAngle(cluster2.points.map { it.lngDeg }).toZodiacDegree()

              logger.trace { "mid1 = $mid1 ${mid1.signDegree}, mid2 = $mid2 ${mid2.signDegree}" }
              val effective = AspectEffectiveModern.isEffective(mid1, mid2, OPPOSITION, 12.0)
              Triple(group1, group2, effective)
            }.filter { (_, _, effective) -> effective }
              .map { (group1, group2, _) ->
                // 分數計算 : group1 裡面所有星 , 與 group2 裡面所有星 , 取 對沖 分數 , 過門檻者，加以平均
                val score = Sets.cartesianProduct(group1.toSet(), group2.toSet()).mapNotNull {
                  val (p1, p2) = it[0] to it[1]
                  aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, OPPOSITION)
                }
                  .map { (_, score) -> score }
                  .average()

                AstroPattern.Confrontation(setOf(group1.toSet(), group2.toSet()), score)
              }
          }?.toSet() ?: emptySet()
      }
    }
  }

  private fun calculateAverageAngle(angles: List<Double>): Double {
    var sumSin = 0.0
    var sumCos = 0.0

    angles.forEach { angle ->
      val radians = Math.toRadians(angle)
      sumSin += sin(radians)
      sumCos += cos(radians)
    }

    val averageRadians = atan2(sumSin, sumCos)
    return (Math.toDegrees(averageRadians) + 360) % 360
  }

  val patterns: Set<IPatternFactory> by lazy {
    setOf(
      grandTrine, kite, tSquared, yod, boomerang,
      goldenYod, grandCross, doubleT, hexagon, wedge,
      mysticRectangle, pentagram, stelliumSign, stelliumHouse, confrontation
    )
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
