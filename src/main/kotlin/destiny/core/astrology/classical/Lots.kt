/**
 * Created by smallufo on 2026-11-13.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.*
import kotlin.reflect.KClass

/**
 * 一個 Lot（[lot]，福點、精神點…）在這張盤上的位置與它的主星。全部以星座計（整宮制）：
 *
 * - [lng]：Lot 的黃經（公式見 [destiny.core.astrology.lngOf]，日夜由呼叫端判定）
 * - [operands]：這張盤實際的公式 ＝ 上升 ＋ plus − minus（夜盤已對調）—— 演出「從 minus 量到 plus 的弧，再從上升量出去」用它
 * - [house]：從上升星座起算的第幾個星座（1–12）—— 與盤面的宮制無關
 * - [lord]：Lot 所在星座的主人（入廟主）
 * - [lordPlace]：主星落在從 Lot 起算的第幾個星座（1 ＝ 與 Lot 同星座）
 * - [lordAspect]：主星與 Lot 的星座相位；null ＝ 不相見（第 2、6、8、12 個星座）—— 主星照應不到它管的事
 * - [planetsByPlace]：從 Lot 起算第 [LotsOptions.places] 個星座（預設 1、4、7、10、11）上的古典行星，只列有星的
 *
 * 只是事實，不給吉凶 —— 評價屬於判斷層（[destiny.core.astrology.classical.rules.IPlanetPattern]）
 */
data class LotPlacement(
  val lot: Lot,
  val lng: ZodiacDegree,
  val operands: LotOperands,
  val house: Int,
  val lord: Planet,
  val lordPlace: Int,
  val lordAspect: Aspect?,
  val planetsByPlace: Map<Int, List<Planet>>,
) : ClassicalFact

/** 兩個 Lot（福點與精神點）的星座關係：[aspect] 是以星座計的相位，null ＝ 不相見（同星座是合） */
data class LotPair(
  val a: Lot,
  val b: Lot,
  val aspect: Aspect?,
) : ClassicalFact

/**
 * Lot 事實的判準。
 *
 * - [lots]：列哪幾個 Lot（預設福點、精神點；其餘五個 Hermetic lots 在一般本命解讀很少用）
 * - [formula]：公式選項（夜盤是否對調，[LotOptions]）
 * - [places]：從 Lot 起算要列哪幾個星座上的行星（預設四角與第 11 個 —— 古典讀財富與事業用的位置）
 * - [planets]：看哪些星
 */
data class LotsOptions(
  val lots: List<Lot> = listOf(Lot.Fortune, Lot.Spirit),
  val formula: LotOptions = LotOptions.DEFAULT,
  val places: Set<Int> = setOf(1, 4, 7, 10, 11),
  val planets: List<Planet> = Planet.classicalList,
) {
  companion object {
    val DEFAULT = LotsOptions()
  }
}

/**
 * Lot 的古典事實。要盤的日夜（[usesSect]）：日夜不明時不出任何一筆（公式日夜不同，沒有日夜就算不出）
 *
 * ⚠️ 刻意**不**掛進 [destiny.core.astrology.classical.rules.ClassicalPatternContext] 的來源清單：那一份也是本命古典事實存表的判準，
 * 多一種事實就得升版、整批重新回填。Lot 只給解說（星盤導演）用，由組盤的一方另外附上
 */
interface ILots : IClassicalFactSource {

  fun getResults(h: IHoroscopeModel, sect: DayNight): List<ClassicalFact>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = sect?.let { getResults(h, it) } ?: emptyList()

  override val usesSect: Boolean get() = true

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(LotPlacement::class, LotPair::class)
}
