/**
 * Created by smallufo on 2019-05-17.
 */
package destiny.astrology

import com.google.common.collect.Sets
import destiny.astrology.Aspect.*
import mu.KotlinLogging
import org.apache.commons.math3.ml.clustering.Cluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.DBSCANClusterer
import org.apache.commons.math3.ml.distance.DistanceMeasure
import java.io.Serializable

class PatternContext(val aspectEffective: IAspectEffective,
                     val aspectsCalculator: IHoroscopeAspectsCalculator) : Serializable {

  private fun Point.signHouse(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): PointSignHouse {
    return posMap.getValue(this).let { iPos ->
      val sign: ZodiacSign = iPos.sign
      val house: Int = IHoroscopeModel.getHouse(iPos.lng, cuspDegreeMap)
      PointSignHouse(this, sign, house)
    }
  }

  val grandTrine = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return aspectsCalculator.getAspectDataSet(posMap, aspects = setOf(TRINE))
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->

          return Sets.combinations(dataSet, 3).asSequence().mapNotNull { threeSets ->
            val (set1, set2, set3) = threeSets.toList().let { Triple(it[0], it[1], it[2]) }
            set1.points.union(set2.points).union(set3.points)
              .takeIf { unionPoints -> unionPoints.size == 3 }
              ?.takeIf { unionPoints ->
                Sets.combinations(unionPoints, 2).all { twoPoint ->
                  val (p1, p2) = twoPoint.toList().let { it[0] to it[1] }
                  aspectEffective.isEffective(p1, posMap.getValue(p1).lng, p2, posMap.getValue(p2).lng, TRINE)
                }
              }
              ?.let { unionPoints ->
                val score = threeSets.takeIf { sets -> sets.all { it.score != null } }
                  ?.map { it.score!! }?.average()
                AstroPattern.GrandTrine(unionPoints, posMap.getValue(set1.points.first()).sign.element, score)
              }
          }.toSet()
        } ?: emptySet()
    }
  } // 大三角


  val kite = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return grandTrine.getPatterns(posMap, cuspDegreeMap)
        .map { it as AstroPattern.GrandTrine }
        .flatMap { grandTrine ->
          // 大三角的 每個點 , 都當作風箏的尾巴，去找是否有對沖的點 (亦即：風箏頭)
          grandTrine.points.map { tail ->
            tail to aspectsCalculator.getPointAspectAndScore(tail, posMap, aspects = setOf(OPPOSITION))
          }.filter { (_, oppoSet) ->
            oppoSet.isNotEmpty()
          }.flatMap { (tail, oppoSet: Set<Triple<Point, Aspect, Double>>) ->
            oppoSet.map { (head, _, oppoScore) ->
              Triple(tail, head, oppoScore)
            }.mapNotNull { (tail, head, oppoScore) ->
              // 每個 head 都需要與 兩翼 SEXTILE

              grandTrine.points.minus(tail).map { wingPoint ->
                wingPoint to aspectEffective.getEffectiveErrorAndScore(head, wingPoint, posMap, SEXTILE)
              }.takeIf { list ->
                list.all { (wing , maybeErrorAndScore) -> maybeErrorAndScore!= null }
              }?.map { (wing , maybeErrorAndScore) -> wing to maybeErrorAndScore!!.second }
                ?.toMap()
                ?.let { map: Map<Point, Double> ->
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

    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return aspectsCalculator.getAspectDataSet(posMap, aspects = twoAspects)
        .takeIf { it.size >= 3 }
        ?.let { pairs ->
          Sets.combinations(pairs, 3).asSequence().filter { threeSet ->
            threeSet.flatMap { it.points }.toSet().size == 3
          }.filter { threeSet ->
            threeSet.filter { it.aspect == OPPOSITION }.size == 1
              && threeSet.filter { it.aspect == SQUARE }.size == 2
          }.map { threeSet ->
            val score: Double? = threeSet.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!! }?.average()
            val oppoPoints = threeSet.first { it.aspect == OPPOSITION }.points
            val squared = threeSet.flatMap { it.points }.toSet().minus(oppoPoints).first().signHouse(posMap, cuspDegreeMap)
            AstroPattern.TSquared(oppoPoints, squared, score)
          }
        }?.toSet() ?: emptySet()

    }
  } // 三刑會沖


  // 上帝之指
  val yod = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return aspectsCalculator.getAspectDataSet(posMap, aspects = setOf(QUINCUNX))
        .takeIf { it.size >= 2 }
        ?.let { dataSet ->
          // 任兩個 QUINCUNX ,
          Sets.combinations(dataSet, 2).asSequence().filter { twoSets: Set<AspectData> ->
            // 確保組合而成的 points 若共有三顆星
            twoSets.flatMap { it.points }.toSet().size == 3
          }.map { twoSets: Set<AspectData> ->
            val (set1: AspectData, set2: AspectData) = twoSets.toList().let { it[0] to it[1] }
            val intersectedPoint = set1.points.intersect(set2.points)
            val (other1: Point, other2: Point) = twoSets.flatMap { it.points }.toSet().minus(intersectedPoint).toList().let { it[0] to it[1] }
            // 確保 另外兩點 形成 60 度
            twoSets to aspectEffective.getEffectiveErrorAndScore(other1, other2, posMap, SEXTILE)
          }
            .filter { (_ , maybeErrorAndScore) -> maybeErrorAndScore != null}
            .map { (twoSets , errorAndScore) -> twoSets to errorAndScore!! }
            .map { (twoSets, errorAndScore) ->
              val score: Double? = twoSets.takeIf { sets -> sets.all { it.score != null } }?.map { it.score!! }?.plus(errorAndScore.second)?.average()
              val (set1: AspectData, set2: AspectData) = twoSets.toList().let { it[0] to it[1] }
              val pointer = set1.points.intersect(set2.points).first().signHouse(posMap, cuspDegreeMap)
              val bottoms: Set<Point> = twoSets.flatMap { it.points }.toSet().minus(pointer.point)
              AstroPattern.Yod(bottoms, pointer, score)
            }
        }?.toSet() ?: emptySet()
    }
  } // 上帝之指

  // 回力鏢 : YOD + 對沖點
  val boomerang = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return yod.getPatterns(posMap, cuspDegreeMap)
        .map { it as AstroPattern.Yod }
        .flatMap { pattern ->
          aspectsCalculator.getPointAspectAndScore(pattern.pointer.point, posMap, posMap.keys, setOf(OPPOSITION))
            .map { (oppoPoint, _, oppoScore) ->

              // 對沖點，還必須與兩翼形成30度
              val validScores: Set<Double> = pattern.bottoms.mapNotNull { bottom ->
                aspectEffective.getEffectiveErrorAndScore(oppoPoint, bottom, posMap, SEMISEXTILE)?.second
              }.toSet()

              Triple(oppoPoint, oppoScore, validScores)
            }
            .filter { (_, _, validScores) -> validScores.size == 2 }
            .map { (oppoPoint, oppoScore, _) ->
              // 分數 : 上帝之指分數 + 對沖分數 + 兩個60度的分數 , 平均
              //val score: Double? = pattern.score?.let { patternScore -> bottoms60ScoreMap.map { sextileScore -> sextileScore.value }.plus(oppoScore).plus(patternScore).average() }
              // 分數 : 上帝之指分數 + 對沖分數 平均
              val score: Double? = pattern.score?.let { patternScore -> setOf(oppoScore).plus(patternScore).average() }
              AstroPattern.Boomerang(pattern, oppoPoint.signHouse(posMap, cuspDegreeMap), score)
            }
        }.toSet()
    }
  } // 回力鏢 : YOD + 對沖點


  val goldenYod = object : IPatternFactory {

    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return aspectsCalculator.getAspectDataSet(posMap, aspects = setOf(BIQUINTILE, QUINTILE))  // 144 , 72
        .takeIf { it.size >= 3 }
        ?.let { dataSet ->
          Sets.combinations(dataSet, 3).asSequence().filter { threePairs ->
            threePairs.flatMap { it.points }.toSet().size == 3
          }
            .filter { threePairs ->
              threePairs.filter { it.aspect == BIQUINTILE }.size == 2
                && threePairs.filter { it.aspect == QUINTILE }.size == 1
            }
            .map { threePairs: Set<AspectData> ->

              val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!! }?.average()

              val bottoms = threePairs.first { it.aspect == QUINTILE }.points
              val pointer = threePairs.flatMap { it.points }.toSet().minus(bottoms).first()
              // val pointerSign = posMap.getValue(pointer).sign
              AstroPattern.GoldenYod(bottoms, pointer.signHouse(posMap, cuspDegreeMap), score)
            }
        }?.toSet() ?: emptySet()
    }
  }


  val grandCross = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return tSquared.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.let { dataSets: Set<AstroPattern> ->
          /** 所有的 [AstroPattern.TSquared] , 找出 頂點( [AstroPattern.TSquared.squared]) , 比對此頂點是否有對沖點
           * 並且要求，對衝點，與三刑的兩角尖，也要相刑
           * 才能確保比較漂亮的 大十字
           * */
          dataSets.asSequence().map { it as AstroPattern.TSquared }
            .flatMap { tSquared ->
              aspectsCalculator.getPointAspect(tSquared.squared.point, posMap, aspects = setOf(OPPOSITION)).keys.mapNotNull { oppo: Point ->

                // oppo Point 還必須與 三刑會沖 兩角尖 相刑 , 才能確保比較漂亮的 大十字

                aspectsCalculator.getPointAspectAndScore(oppo, posMap, tSquared.oppoPoints, setOf(SQUARE))
                  .takeIf { it.size == 2 }
                  ?.let { twoSquared: Set<Triple<Point, Aspect, Double>> ->
                    // 一個 T-Squared 的分數
                    val tSquaredScore = tSquared.score
                    // 加上其對衝點 , 與此 T-Squared 兩底點的 squared 分數
                    val twinSquaredScore: List<Double> = twoSquared.map { it.third }
                    // 三個值 , 平均
                    val score: Double? = tSquaredScore?.let { twinSquaredScore.plus(it).average() }

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


  val doubleT = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return tSquared.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.asSequence()
        ?.map { pattern -> pattern as AstroPattern.TSquared }
        ?.let { dataSet: Sequence<AstroPattern.TSquared> ->
          Sets.combinations(dataSet.toSet(), 2).asSequence().filter { twoPatterns ->
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
            val score: Double? = twoPatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!! }?.average()
            AstroPattern.DoubleT(twoPatterns, score)
          }
        }?.toSet() ?: emptySet()
    }
  } // DoubleT


  // 六芒星
  val hexagon = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return grandTrine.getPatterns(posMap, cuspDegreeMap)
        .takeIf { it.size >= 2 }
        ?.asSequence()
        ?.map { pattern -> pattern as AstroPattern.GrandTrine }
        ?.let { dataSet ->
          Sets.combinations(dataSet.toSet(), 2).asSequence().filter { twoSets ->
            // 先確保 有六顆星
            twoSets.flatMap { it.points }.toSet().size == 6
          }.filter { twoTrines ->
            // 兩組大三角中，每顆星都能在另一組中找到對沖的星
            val (g1, g2) = twoTrines.toList().let { it[0] to it[1] }
            g1.points.all { p ->
              aspectsCalculator.getPointAspect(p, posMap, g2.points, aspects = setOf(OPPOSITION)).size == 1
            }
          }.map { twoTrines: Set<AstroPattern.GrandTrine> ->
            val score: Double? = twoTrines.takeIf { trines -> trines.all { it.score != null } }?.map { it.score!! }?.average()
            AstroPattern.Hexagon(twoTrines, score)
          }
        }?.toSet() ?: emptySet()
    }
  } // 六芒星


  // 180 沖 , 逢 第三顆星 , 以 60/120 介入，緩和局勢
  val wedge = object : IPatternFactory {
    // 只比對 180 , 60 , 120 三種度數
    private val threeAspects = setOf(OPPOSITION, SEXTILE, TRINE)

    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      val dataSet: Set<AspectData> = aspectsCalculator.getAspectDataSet(posMap, aspects = threeAspects)

      return dataSet.takeIf { it.size >= 3 }
        ?.let {
          Sets.combinations(dataSet, 3).asSequence().filter { threePairs ->
            // 確保三種交角都有
            threePairs.map { dataSet -> dataSet.aspect }.containsAll(threeAspects)
          }.filter { threePairs ->
            // 總共只有三顆星介入
            threePairs.flatMap { it.points }.toSet().size == 3
          }.map { threePairs ->
            val score = threePairs.takeIf { pairs -> pairs.all { it.score != null } }?.map { it.score!! }?.average()

            val oppoPoints = threePairs.first { it.aspect == OPPOSITION }.points
            val moderator = threePairs.flatMap { it.points }.toSet().minus(oppoPoints).iterator().next()
            // val moderatorSign = posMap.getValue(moderator).sign
            AstroPattern.Wedge(oppoPoints, moderator.signHouse(posMap, cuspDegreeMap), score)
          }.toSet()
        } ?: emptySet()
    }
  } // 對衝，逢 Trine / Sextile , 形成 直角三角形


  /** [AstroPattern.MysticRectangle] 神秘長方形 */
  val mysticRectangle = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
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
              aspectEffective.getEffectiveErrorAndScore(wedge1.moderator.point, wedge2.moderator.point, posMap, OPPOSITION)?.second
            } else {
              null
            }
          }.filter { (_ , maybeScore) -> maybeScore != null }
            .map { (twoWedges , maybeScore) -> twoWedges to maybeScore!! }
            .map { (twoWedges , oppoScore) ->
              val unionPoints = twoWedges.flatMap { it.points }.toSet()
              // 分數 : 以兩組 wedge 個別分數 , 加上 moderator 對沖分數 , 三者平均
              val score: Double? = twoWedges.takeIf { pattern -> pattern.all { it.score != null } }?.map { it.score!! }?.plus(oppoScore)?.average()
              AstroPattern.MysticRectangle(unionPoints, score)
            }.toSet()
        } ?: emptySet()
    }
  } // 神秘長方形


  // 五芒星 , 144 , 72
  val pentagram = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {

      return goldenYod.getPatterns(posMap, cuspDegreeMap)
        .takeIf { patterns -> patterns.size >= 5 }
        ?.let { patterns ->
          Sets.combinations(patterns, 5).asSequence()
            .filter { fiveSet -> fiveSet.flatMap { it.points }.toSet().size == 5 }
            .map { fivePatterns ->
              val score: Double? = fivePatterns.takeIf { patterns -> patterns.all { it.score != null } }?.map { it.score!! }?.average()
              val fivePoints = fivePatterns.flatMap { it.points }.toSet()
              AstroPattern.Pentagram(fivePoints, score)
            }
        }?.toSet() ?: emptySet()
    }
  } // 五芒星 (尚未出現範例)

  // 群星聚集 某星座 (至少四顆星)
  val stelliumSign = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return posMap.entries.groupBy { (_, pos) -> pos.sign }
        .filter { (_, list) -> list.size >= 4 }
        .map { (sign, list: List<Map.Entry<Point, IPos>>) ->
          val points = list.map { it.key }.toSet()
          /** 分數算法： 以該星座內 [Aspect.CONJUNCTION] 分數平均 */

          val score = Sets.combinations(points, 2).asSequence().map { pair ->
            val (p1, p2) = pair.toList().let { it[0] to it[1] }
            // 對於「同一星座內，但是沒有形成合相的雙星」其分數雖然是零分，但是不要過濾 , 就給 零分
            aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, CONJUNCTION)?.second?:0.0
          }.average()

          AstroPattern.StelliumSign(points, sign, score)
        }.toSet()
    }
  }

  // 群星聚集 某宮位 (至少四顆星)
  val stelliumHouse = object : IPatternFactory {
    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      return posMap.map { (point, pos) -> point to IHoroscopeModel.getHouse(pos.lng, cuspDegreeMap) }
        .groupBy { (_, house) -> house }
        .filter { (_, list: List<Pair<Point, Int>>) -> list.size >= 4 }
        .map { (house, list: List<Pair<Point, Int>>) ->
          val points = list.map { it.first }.toSet()
          /** 分數算法： 以該宮位內 [Aspect.CONJUNCTION] 分數平均 */
          val score = Sets.combinations(points, 2).asSequence().map { pair ->
            val (p1, p2) = pair.toList().let { it[0] to it[1] }
            aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, CONJUNCTION)?.second ?: 0.0
          }.average()

          AstroPattern.StelliumHouse(points, house, score)
        }.toSet()
    }
  }

  inner class PointCluster(val point: Point, val lngDeg: Double) : Clusterable {
    override fun getPoint(): DoubleArray {
      return arrayOf(lngDeg).toDoubleArray()
    }
  }

  // 星群對峙 : 兩組 3顆星以上的合相星群 彼此對沖
  val confrontation = object : IPatternFactory {

    override fun getPatterns(posMap: Map<Point, IPos>, cuspDegreeMap: Map<Int, Double>): Set<AstroPattern> {
      val pointMap = posMap.map { (point, pos) -> PointCluster(point, pos.lng) }
      val cluster = DBSCANClusterer<PointCluster>(6.0, 2
        , DistanceMeasure { arr1, arr2 -> IHoroscopeModel.getAngle(arr1[0], arr2[0]) }
      )

      return cluster.cluster(pointMap).let { list: List<Cluster<PointCluster>> ->
        list
          .takeIf { clusters -> clusters.size >= 2 }
          ?.let { clusters ->
            logger.trace("clusters size >=2 !! : {}", clusters.size)
            Sets.combinations(clusters.toSet(), 2).asSequence().map { twoClusters: Set<Cluster<PointCluster>> ->
              val (cluster1: Cluster<PointCluster>, cluster2: Cluster<PointCluster>) = twoClusters.toList().let { it[0] to it[1] }
              val group1 = cluster1.points.map { it.point }
              val group2 = cluster2.points.map { it.point }
              logger.trace("group1 = {} , group2 = {}", group1, group2)
              // 兩個群組的中點是否對沖
              val mid1 = cluster1.points.map { it.lngDeg }.average()
              val mid2 = cluster2.points.map { it.lngDeg }.average()

              logger.trace("mid1 = {} {} , mid2 = {} {}", mid1, ZodiacSign.getSignAndDegree(mid1), mid2, ZodiacSign.getSignAndDegree(mid2))
              val effective = AspectEffectiveModern.isEffective(mid1, mid2, OPPOSITION, 12.0)
              Triple(group1, group2, effective)
            }.filter { (_, _, effective) -> effective }
              .mapNotNull { (group1, group2, _) ->
                // 分數計算 : group1 裡面所有星 , 與 group2 裡面所有星 , 取 對沖 分數 , 過門檻者，加以平均
                val score = Sets.cartesianProduct(group1.toSet(), group2.toSet()).mapNotNull {
                  val (p1, p2) = it[0] to it[1]
                  aspectEffective.getEffectiveErrorAndScore(p1, p2, posMap, OPPOSITION)
                }
                  .map { (_, score) -> score }
                  .average()

                if (score.isNaN()) {
                  // FIXME 在極端狀況下 (群星聚集在黃道零度旁)， 角度取平均，可能不會是 0 的附近，可能會被 360之前的數值給拖累
                  //  例如 1821-03-26 , 5:55 AM , 就出現這種狀況 ( from painters )
                  //  group1 = [太陽, 水星, 木星, 冥王星] , 其中 冥王星 357度, 其他三星都是0度左右，一平均起來 mid1 = 93度 ,
                  //  group2 = [月亮, 天王星, 海王星] 這是正常的 , mid2 = 271
                  //  就變成與 group2 對沖 , 其實並沒有！
                  logger.warn("score is NaN !! group1 = {} , group2 = {}", group1, group2)
                  null
                } else {
                  AstroPattern.Confrontation(setOf(group1.toSet(), group2.toSet()), score)
                }
              }
          }?.toSet() ?: emptySet()
      }
    }
  }

  val patterns: Set<IPatternFactory> = setOf(
    grandTrine, kite, tSquared, yod, boomerang,
    goldenYod, grandCross, doubleT, hexagon, wedge,
    mysticRectangle, pentagram, stelliumSign, stelliumHouse, confrontation
  )

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
