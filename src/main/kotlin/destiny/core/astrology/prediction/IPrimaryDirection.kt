/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.IHoroscopeModel
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Serializable


/** 時間鑰匙的介面，定義了弧角如何轉換為時間 (維度E) */
interface ITimeKey {
  /** 年數 → 弧角 */
  fun getArc(years: Double): Double
  /** 弧角 → 年數 */
  fun getYears(arc: Double): Double
}

/** 托勒密之鑰：1度赤經 = 1年生命 */
@Serializable
object PtolemyKey : ITimeKey {
  override fun getArc(years: Double): Double = years
  override fun getYears(arc: Double): Double = arc
}

/** Naibod之鑰：1年 = 0.9856度 (太陽每日平均移動速度) */
@Serializable
object NaibodKey : ITimeKey {
  private const val NAIBOD_ARC_PER_YEAR = 360.0 / TROPICAL_YEAR_DAYS // 0.98564736
  override fun getArc(years: Double): Double = years * NAIBOD_ARC_PER_YEAR
  override fun getYears(arc: Double): Double = arc / NAIBOD_ARC_PER_YEAR
}

/**
 * 維度A — 弧角量法框架，決定緯度/位置如何投影成推進弧。
 * 目前僅 [RIGHT_ASCENSION] 有實作，其餘為未來保留。
 */
enum class PdFrame {
  /** 純赤經差，無半弧修正（最簡，古典 in-RA 法）。已實作。 */
  RIGHT_ASCENSION,
  /** Placidus 半弧法（到 Asc/MC 有閉式解）。尚未實作。 */
  PLACIDUS,
  /** Regiomontanus 位置圈投影。尚未實作。 */
  REGIOMONTANUS,
  /** Campanus prime-vertical 投影。尚未實作。 */
  CAMPANUS,
}

/**
 * 主限法的「方法」—— 把 [黃道/世俗] × [框架] × [是否帶緯度] 收斂成一個帶參數的密封型別。
 *
 * 概念同 Return 的 Solar/Lunar：不同變形攜帶不同參數，且無效組合無法被構造。
 */
sealed interface PrimaryDirectionMethod {
  /** 量法框架 (維度A) */
  val frame: PdFrame
  /**
   * 是否使用星體自身黃緯 (維度C)。false = 古典「無緯度」，投影至黃道 (緯度0)。
   *
   * 黃緯僅適用於星體**本體**（即合相點）；非合相的相位點是黃道面上的幾何點，恆為緯度 0，
   * 不隨 promissor 的黃緯平移（Bianchini 式比例緯度等罕見流派不在此列）。
   */
  val withLatitude: Boolean

  /** 黃道主限：相位形成於黃道；[frame] 決定弧角幾何。 */
  data class Zodiacal(
    override val frame: PdFrame = PdFrame.RIGHT_ASCENSION,
    override val withLatitude: Boolean = false,
  ) : PrimaryDirectionMethod

  /** 世俗主限：星體在宮位空間推進（含 mundane / rapt parallels）。尚未實作。 */
  data class Mundane(
    override val frame: PdFrame = PdFrame.PLACIDUS,
    override val withLatitude: Boolean = true,
  ) : PrimaryDirectionMethod
}

/** 代表一個主限法事件 */
data class DirectionEvent(
  val significator: AstroPoint,
  val promissor: AstroPoint,
  val aspect: Aspect,
  /** 推進的弧角 (赤經度數) */
  val arc: Double,
  /** 對應的年數 (經 [ITimeKey] 換算) */
  val years: Double,
  /** 事件發生的約略時間 */
  val eventGmt: GmtJulDay,
  /** 是否順推 (true) 或逆推 (false)，同 [Conversable] 慣例 */
  val forward: Boolean,
)

interface IPrimaryDirection {
  /**
   * 計算主限法事件。
   *
   * @param method  方法 (黃道/世俗 × 框架 × 帶緯度)，預設黃道-赤經法
   * @param forward 順推 (true) 或逆推 (false)，同 [Conversable] 慣例
   * @param timeKey 時間鑰匙，預設 [PtolemyKey]
   */
  fun getDirectionEvents(
    natalChart: IHoroscopeModel,
    significators: Set<AstroPoint>,
    promissors: Set<AstroPoint>,
    aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet(),
    method: PrimaryDirectionMethod = PrimaryDirectionMethod.Zodiacal(),
    forward: Boolean = true,
    timeKey: ITimeKey = PtolemyKey,
  ): List<DirectionEvent>
}
