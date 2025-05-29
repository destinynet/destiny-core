/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.core.astrology

import com.google.common.collect.Sets
import destiny.core.calendar.GmtJulDay
import destiny.tools.Score.Companion.toScore
import java.util.*

/**
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
 */
interface IRelativeTransit {

  /**
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了另外算 270度
   * TODO : 目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT julDay
   */
  fun getRelativeTransit(
    transitStar: Star,
    relativeStar: Star,
    angle: Double,
    gmtJulDay: GmtJulDay,
    isForward: Boolean
  ): GmtJulDay?


  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return 傳回一連串的 gmtJulDays
   */
  fun getPeriodRelativeTransitGmtJulDays(
    transitStar: Star,
    relativeStar: Star,
    fromJulDay: GmtJulDay,
    toJulDay: GmtJulDay,
    angle: Double
  ): List<GmtJulDay> {

    return generateSequence(getRelativeTransit(transitStar, relativeStar, angle, fromJulDay, true)) {
      getRelativeTransit(transitStar, relativeStar, angle, it + 0.000001, true)
    }.takeWhile { it < toJulDay }
      .toList()
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   *
   * result 有可能為 empty , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
   *
   * @return 傳回的 Pair , 前者為 GMT 時間，後者為角度
   */
  fun getNearestRelativeTransitGmtJulDay(
    transitStar: Star,
    relativeStar: Star,
    fromGmtJulDay: GmtJulDay,
    angles: Set<Double>,
    forward: Boolean
  ): Pair<GmtJulDay, Double>? {
    /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */

    val realAngles: List<Double> = angles
      .flatMap {
        if (it != 0.0)
          listOf(it, 360.0 - it)
        else
          listOf(it)
      }
      .toList()

    return realAngles.mapNotNull { angle ->
      getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, forward)?.let { resultGmtJulDay ->
        resultGmtJulDay to angle
      }
    }
      .sortedBy { (julDay, _) -> julDay }
      .let {
        if (forward)
          it.firstOrNull()  // 順推，取第一個（最接近當下）
        else
          it.lastOrNull()   // 逆推，取最後一個（最接近當下）
      }?.let { (julDay, angle) ->
        if (angle > 180)
          julDay to 360 - angle
        else
          julDay to angle
      }

  }


  /**
   * 找尋下一個與 [transitStar] 形成交角的資料
   */
  fun getNearestRelativeTransitGmtJulDay(transitStar: Star, relativeStars: Set<Star>, fromGmtJulDay: GmtJulDay, aspects: Set<Aspect>, forward: Boolean): IAspectData? {

    return relativeStars.filter { it != transitStar }.mapNotNull { eachOther ->
      getNearestRelativeTransitGmtJulDay(transitStar, eachOther, fromGmtJulDay, aspects.map { it.degree }.toSet(), forward)
        ?.let { (gmt, deg) -> Triple(eachOther, gmt, deg) }
    }.map { (other, gmt, deg) -> AspectData.of(transitStar, other, Aspect.getAspect(deg)!!, 0.0, 0.0.toScore(), null, gmt) }
      .let { list ->
        if (forward)
          list.minByOrNull { it.gmtJulDay }
        else
          list.maxByOrNull { it.gmtJulDay }
      }
  }

  /**
   * 找出在指定時間範圍內，一組星曜 (`stars`) 之間彼此形成指定交角 (`angles`) 的所有時刻。
   *
   * @param stars 一組星曜。
   * @param anglesInput 一組基礎交角 (例如 0, 60, 90, 120, 180 度)。函數內部會處理對稱角度 (例如 90 也會考慮 270)。
   * @param fromGmtJulDay 開始搜索的 GMT Julian Day。
   * @param toGmtJulDay 結束搜索的 GMT Julian Day (不包含此時刻)。
   * @return 一個 AspectData 的序列，依時間排序。AspectData 包含形成交角的星曜對和發生時間。
   */
  fun mutualAspectingEvents(stars: Set<Star>, anglesInput: Set<Double>, fromGmtJulDay: GmtJulDay, toGmtJulDay: GmtJulDay): Sequence<AspectData> {

    data class AspectEventCandidate(
      val star1: Star,
      val star2: Star,
      val angleUsed: Double, // 實際用於 getRelativeTransit 的角度
      val time: GmtJulDay
    ) : Comparable<AspectEventCandidate> {
      // PriorityQueue 會根據這個比較來排序，時間早的優先
      override fun compareTo(other: AspectEventCandidate): Int = this.time.compareTo(other.time)
    }

    // 處理角度的對稱性，例如 90 度也考慮 270 度。
    // 0 度 (合相) 和 180 度 (對分相) 是它們自己的對稱。
    val effectiveAngles: List<Double> = anglesInput
      .flatMap { angle ->
        if (angle != 0.0 && angle != 180.0) {
          listOf(angle, (360.0 - angle) % 360.0) // 確保角度在 0-360 範圍
        } else {
          listOf(angle)
        }
      }
      .distinct() // 去除重複角度

    if (stars.size < 2 || effectiveAngles.isEmpty()) {
      return emptySequence()
    }

    val priorityQueue = PriorityQueue<AspectEventCandidate>()

    // 1. 初始化 PriorityQueue：
    //    對於每一對星曜和每一個有效角度，計算它們在 fromGmtJulDay 之後的第一次交角時間。
    val starPairs = Sets.combinations(stars, 2)

    for (pairSet in starPairs) {
      val pairList = pairSet.toList() // Set<Star> to List<Star>
      val s1 = pairList[0]
      val s2 = pairList[1]

      for (angle in effectiveAngles) {
        // 呼叫已有的核心方法
        val firstOccurrenceTime = getRelativeTransit(s1, s2, angle, fromGmtJulDay, true)
        if (firstOccurrenceTime != null && firstOccurrenceTime < toGmtJulDay) {
          priorityQueue.add(AspectEventCandidate(s1, s2, angle, firstOccurrenceTime))
        }
      }
    }

    return generateSequence {
      priorityQueue.poll()?.also { earliestEvent ->
        // 當一個事件被提取後 (它是當前最早的)，
        // 我們需要為產生該事件的同一對星曜和同一角度，計算它們的下一次發生時間，
        // 並將其放回 PriorityQueue。
        // 搜尋的起始時間是剛提取事件的時間加上一個微小量，以避免找到同一個事件。
        val nextSearchTime = earliestEvent.time + 0.000001 // 小增量確保向前推進
        val nextOccurrenceTime = getRelativeTransit(
          earliestEvent.star1,
          earliestEvent.star2,
          earliestEvent.angleUsed,
          nextSearchTime,
          true
        )

        if (nextOccurrenceTime != null && nextOccurrenceTime < toGmtJulDay) {
          priorityQueue.add(
            AspectEventCandidate(
              earliestEvent.star1,
              earliestEvent.star2,
              earliestEvent.angleUsed,
              nextOccurrenceTime
            )
          )
        }
      }
    }.map { eventCandidate ->
      val angle = eventCandidate.angleUsed
      val pattern = PointAspectPattern(listOf(eventCandidate.star1, eventCandidate.star2), angle, null, 0.0)
      AspectData(pattern, null, 0.0, null, eventCandidate.time)
    }

  }

}
