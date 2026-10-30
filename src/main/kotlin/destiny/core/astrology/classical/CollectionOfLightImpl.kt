/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.IPointAspectPattern.AspectType.APPLYING
import kotlin.math.abs

/**
 * Collection of Light。定義（William Lilly, Christian Astrology）：
 * 1. 兩個徵象星彼此不相見（不成相位，或已經分離）
 * 2. 兩者都比收集者快，且都在容許度內（古典：兩星光芒半徑相加）入相位到收集者
 * 3. 傳統上還要求兩者都以 essential dignities 接納收集者 —— 各自以哪一種接納記在結果裡，[ICollectionOfLight.getResult] 的 receivedOnly 篩選
 *
 * 同一顆收集者有三顆以上的快星入相位時，取最先成形的兩顆（容許度 ÷ 相對速度）。
 *
 * 2026-10-31 取代 2014 年的舊版（依 [IBesieged] 找時間上前後兩顆星）。舊版的問題：
 * - 「時間上前一顆」是已經成形過的相位（出相位），與「兩者都入相位」的定義相反
 * - 找到兩顆星之後沒有檢查容許度與入相位，也沒有排除兩顆快星彼此本來就成相位
 * - 判斷兩顆星不重複的寫法有誤（`list.size == setOf(list).size` 永遠是 2 ≠ 1），所以永遠回傳 null
 * - 另有一種「以落陷、入弱接納」的收集，傳統上沒有這種接納，已拿掉
 */
class CollectionOfLightImpl(
  private val essentialImpl: IEssential,
  private val aspectCalculator: IAspectCalculator,
  private val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet(),
) : ICollectionOfLight {

  override fun getResult(planet: Planet, h: IHoroscopeModel, receivedOnly: Boolean): CollectionOfLight? {
    if (planet !in Planet.classicalSet) return null
    val speedOf = { p: Planet -> h.getStarPosition(p)?.speedLng }
    val speed = speedOf(planet) ?: return null

    data class Approach(val planet: Planet, val pattern: IPointAspectPattern, val days: Double)

    // 比收集者快、在容許度內、正在入相位的古典行星，依成形先後排
    val approaching = with(aspectCalculator) {
      Planet.classicalList.filter { it !== planet }.mapNotNull { other ->
        val otherSpeed = speedOf(other) ?: return@mapNotNull null
        if (abs(otherSpeed) <= abs(speed)) return@mapNotNull null
        h.getAspectPattern(setOf(planet, other), aspects)
          ?.takeIf { it.aspectType == APPLYING }
          ?.let { Approach(other, it, it.orb / abs(otherSpeed - speed)) }
      }
    }.sortedBy { it.days }

    val pairs = approaching.flatMapIndexed { i, a -> approaching.drop(i + 1).map { b -> a to b } }
    return pairs.asSequence().mapNotNull { (a, b) ->
      val between = with(aspectCalculator) { h.getAspectPattern(setOf(a.planet, b.planet), aspects) }
      // 兩顆快星自己就會成形：不需要收集者
      if (between?.aspectType == APPLYING) return@mapNotNull null
      val (ra, rb) = with(essentialImpl) { a.planet.receivingDignityOf(planet, h) to b.planet.receivingDignityOf(planet, h) }
      CollectionOfLight(planet, a.pattern, b.pattern, between, ra, rb)
        .takeIf { !receivedOnly || it.received }
    }.firstOrNull()
  }
}
