/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.core.calendar.event

import destiny.core.astrology.eclipse.LunarType
import destiny.core.calendar.SolarTimeDto
import destiny.tools.serializers.DoubleFourDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 一次月食的動畫資料（destiny-charts-web `<lunar-eclipse-chart>`），全部伺服器算好。
 *
 * 兩個畫面共用一條時間軸：
 * - 影子盤：月亮穿過地球的半影與本影 —— 說明月食是什麼、食到多深
 * - 世界地圖：此刻月亮在哪半個地球的地平線上 —— 說明哪裡看得到
 *
 * 時間一律 epoch 毫秒（UTC）；民用時間由 [civilOffsets] 分段換算（同 [SolarTimeDto]）。
 */
@Serializable
data class LunarEclipseDto(
  val lunarType: LunarType,
  /** 顯示名（依請求語系），例：月全食 */
  val typeName: String,

  /** 接觸時刻，依時間遞增；偏食沒有 U2／U3，半影食只有 P1、食甚、P4 */
  val contacts: List<Contact>,

  /** 動畫窗口：P1 之前、P4 之後各留一段 */
  val windowFromMs: Long,
  val windowToMs: Long,

  /** 窗口內的民用時差分段；第一段 fromMs = windowFromMs */
  val civilOffsets: List<SolarTimeDto.CivilOffset>,

  /**
   * 「月亮在地平線上」的門檻：某地看月亮的**地心**高度大於這個值（度）就算看得到。
   * 已計入月亮視差、大氣折射與月亮半徑（月亮上緣剛露出地平線）。
   */
  @Serializable(with = DoubleFourDecimalSerializer::class) val moonUpAltDeg: Double,

  /** 市場城市看這次月食的情形（swisseph 逐地計算，權威值） */
  val home: Home,

  /** 窗口內等間隔取樣（含兩端），依時間遞增 */
  val samples: List<Sample>,
) {

  /** 月食的接觸點 */
  enum class ContactKind {
    /** 半影開始 */
    P1,
    /** 本影開始（偏食開始） */
    U1,
    /** 全食開始 */
    U2,
    /** 食甚 */
    MAX,
    /** 全食結束 */
    U3,
    /** 本影結束（偏食結束） */
    U4,
    /** 半影結束 */
    P4,
  }

  @Serializable
  data class Contact(val kind: ContactKind, val tMs: Long)

  @Serializable
  data class Home(
    /** 顯示名，例：台北 */
    val name: String,
    @Serializable(with = DoubleFourDecimalSerializer::class) val lat: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val lng: Double,
    /** 這些接觸點發生時，月亮在當地的地平線上 */
    val visible: List<ContactKind>,
    /** 由 [visible] 歸納的結論（[visibilityOf]） */
    val zone: EclipseVisibility,
  )

  companion object {
    /**
     * 以「看得出來的那一段」計 —— 有本影就是 U1–U4，半影食以 P1–P4。
     * @param contacts 這次月食的接觸點（依時間）
     * @param visible 其中月亮在地平線上的那些
     */
    fun visibilityOf(contacts: List<ContactKind>, visible: Collection<ContactKind>): EclipseVisibility {
      val (from, to) = if (ContactKind.U1 in contacts) ContactKind.U1 to ContactKind.U4 else ContactKind.P1 to ContactKind.P4
      return EclipseVisibility.of(contacts.subList(contacts.indexOf(from), contacts.indexOf(to) + 1).map { it in visible })
    }
  }

  /**
   * 影子盤的座標：以地影中心為原點、從地心看出去的角距（度），x 往東、y 往北（天球赤道座標）。
   * 前端畫「仰望天空」的方位時，東在左。
   */
  @Serializable
  data class Sample(
    val tMs: Long,
    @Serializable(with = DoubleFourDecimalSerializer::class) val x: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val y: Double,
    /** 本影、半影、月亮的角半徑（度） */
    @Serializable(with = DoubleFourDecimalSerializer::class) val umbraR: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val penumbraR: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val moonR: Double,
    /** 本影食分：月面直徑被本影蓋住的比例；未進本影為負 */
    @Serializable(with = DoubleFourDecimalSerializer::class) val umbralMag: Double,
    /** 月下點（月亮在天頂的地點）；經度東經為正，(−180, 180] */
    @Serializable(with = DoubleFourDecimalSerializer::class) val subLat: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val subLng: Double,
  )
}
