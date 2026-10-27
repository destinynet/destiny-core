/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.core.calendar.event

import destiny.core.astrology.eclipse.SolarType
import destiny.core.calendar.SolarTimeDto
import destiny.tools.serializers.DoubleFourDecimalSerializer
import destiny.tools.serializers.DoubleSixDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 一次日食的動畫資料（destiny-charts-web `<solar-eclipse-chart>`）。
 *
 * 日食各地看到的都不一樣，所以這裡給的不是「某地的結果」，而是描述月影的**貝索根數**（Besselian elements）：
 * 有了它，任一地點、任一時刻的食分、接觸時刻與月影的地面輪廓都是幾行算式，前端即時算。
 * 只有市場城市（[home]）的結論由伺服器以 swisseph 逐地計算，那是寫進 HTML 的權威值。
 *
 * 基本面＝通過地心、垂直於影軸（月 → 日）的平面；長度以地球赤道半徑為單位。
 * 時間一律 epoch 毫秒（UTC）；民用時間由 [civilOffsets] 分段換算（同 [SolarTimeDto]）。
 */
@Serializable
data class SolarEclipseDto(
  val solarType: SolarType,
  /** 顯示名（依請求語系），例：日全食 */
  val typeName: String,

  /** 全球的接觸時刻，依時間遞增；偏食沒有中心食 */
  val contacts: List<Contact>,

  /** 食甚點（影軸最接近地心時，月影軸與地面的交點；偏食為食分最大的地點） */
  @Serializable(with = DoubleFourDecimalSerializer::class) val greatestLat: Double,
  @Serializable(with = DoubleFourDecimalSerializer::class) val greatestLng: Double,
  /** 食甚點的食分（太陽直徑被遮住的比例；全食 ≥ 1） */
  @Serializable(with = DoubleFourDecimalSerializer::class) val greatestMagnitude: Double,

  /** 動畫窗口：全球偏食開始之前、結束之後各留一段 */
  val windowFromMs: Long,
  val windowToMs: Long,

  /** 窗口內的民用時差分段；第一段 fromMs = windowFromMs */
  val civilOffsets: List<SolarTimeDto.CivilOffset>,

  /** 半影、本影圓錐的半頂角正切；一場日食之內的變化小於 1e-7，取食甚時的值 */
  val tanF1: Double,
  val tanF2: Double,

  /** 「太陽在地平線上」的門檻：太陽中心的幾何高度大於這個值（度）＝視圓面上緣在地平線上 */
  val sunUpAltDeg: Double,

  /** 市場城市看這次日食的情形（swisseph 逐地計算，權威值） */
  val home: Home,

  /** 窗口內等間隔取樣的貝索根數（含兩端），依時間遞增 */
  val samples: List<Sample>,
) {

  /** 全球的接觸點 */
  enum class ContactKind {
    /** 月影第一次碰到地球：全球最早看得到偏食 */
    BEGIN,
    /** 影軸第一次碰到地球：中心食（全食／環食）開始 */
    CENTRAL_BEGIN,
    /** 食甚 */
    MAX,
    /** 影軸最後離開地球 */
    CENTRAL_END,
    /** 月影最後離開地球 */
    END,
  }

  @Serializable
  data class Contact(val kind: ContactKind, val tMs: Long)

  /** 某地的接觸點：初虧、食既、食甚、生光、復圓 */
  enum class LocalContactKind { C1, C2, MAX, C3, C4 }

  @Serializable
  data class LocalContact(val kind: LocalContactKind, val tMs: Long)

  @Serializable
  data class Home(
    /** 顯示名，例：台北 */
    val name: String,
    @Serializable(with = DoubleFourDecimalSerializer::class) val lat: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val lng: Double,
    /** 當地看到的是哪一種（全食帶外的地方都只是偏食）；當地完全沒有食為 null */
    val localType: SolarType?,
    /** 當地的接觸時刻（依時間）；當地完全沒有食為空 */
    val contacts: List<LocalContact>,
    /** 這些接觸點發生時，太陽在當地的地平線上 */
    val visible: List<LocalContactKind>,
    /** 當地食甚時的食分、遮蔽率（面積比） */
    @Serializable(with = DoubleFourDecimalSerializer::class) val magnitude: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val obscuration: Double,
    /** 由 [visible] 歸納的結論 */
    val zone: EclipseVisibility,
  )

  /**
   * 某一時刻的貝索根數。
   * - [x]、[y]：影軸在基本面上的位置（x 往東、y 往北）
   * - [d]：影軸的赤緯（度）；[mu]：影軸的格林威治時角（度，展開成連續值，不在 360 跳）
   * - [l1]、[l2]：半影、本影在基本面上的半徑；[l2] < 0 是全食（本影圓錐的頂點在地球後方），> 0 是環食
   */
  @Serializable
  data class Sample(
    val tMs: Long,
    @Serializable(with = DoubleSixDecimalSerializer::class) val x: Double,
    @Serializable(with = DoubleSixDecimalSerializer::class) val y: Double,
    @Serializable(with = DoubleSixDecimalSerializer::class) val d: Double,
    @Serializable(with = DoubleSixDecimalSerializer::class) val mu: Double,
    @Serializable(with = DoubleSixDecimalSerializer::class) val l1: Double,
    @Serializable(with = DoubleSixDecimalSerializer::class) val l2: Double,
  )
}
