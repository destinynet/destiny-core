/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 月亮的離與合：月亮最後離開的那個相位（[separating]，希臘化的 aporrhoia）與下一個要成形的相位（[applying]，synaphē）。
 * 希臘化把它讀成「從哪裡來、往哪裡去」—— 離開的那顆星是過去的背景，迎上的那顆星是接下來的方向。
 * 兩者至少有一個才成立；兩者都沒有就是月亮在 [LunarConnectionOptions.window] 內孤立（不另列，空亡另見 [IVoidCourse]）
 */
data class LunarConnection(
  val separating: Link?,
  val applying: Link?,
) : ClassicalFact {
  init {
    require(separating != null || applying != null)
  }

  /**
   * [planet] 與月亮的 [aspect]：[orb] 是盤面時刻離準確還差（或已過）幾度，[days] 是距準確幾天（正數；離相位是幾天前、入相位是幾天後）
   */
  data class Link(val planet: Planet, val aspect: Aspect, val orb: Double, val days: Double)
}

/**
 * 月亮離與合的判準。
 *
 * - [window]：月亮在前後多少度的行程內（以月亮自己的黃經移動量計）
 * - [aspects]：算哪些相位（托勒密五相位）
 * - [withinSign]：只看月亮在目前星座內的離與合（入星座之後離開的、出星座之前成形的）
 */
data class LunarConnectionOptions(
  val window: Double = 30.0,
  val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
  val withinSign: Boolean = false,
) {
  companion object {
    /** 希臘化：月亮前後 30° 的行程（約兩天半），跨星座也算 */
    val HELLENISTIC = LunarConnectionOptions()

    /** 只看目前星座內（卜卦的讀法：月亮離開星座之前的最後一個相位，與空亡的判斷同一個範圍） */
    val WITHIN_SIGN = LunarConnectionOptions(withinSign = true)
  }
}

/** 月亮的離與合 */
interface ILunarConnection : IClassicalFactSource {

  /** 盤上月亮的離與合；兩者都沒有（或缺月亮位置）為 null */
  fun getConnection(h: IHoroscopeModel): LunarConnection?

  fun getResults(h: IHoroscopeModel): List<LunarConnection> = listOfNotNull(getConnection(h))

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(LunarConnection::class)
}
