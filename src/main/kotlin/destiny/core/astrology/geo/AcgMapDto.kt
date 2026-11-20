/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Planet
import destiny.core.calendar.ILatLng
import destiny.core.calendar.toEpochMilliseconds
import destiny.tools.truncate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * ACG 地圖（AstroCartography）的資料合約：某一時刻的天空投影到地表，全部伺服器算好，前端只負責投影與繪製。
 *
 * 時刻可以是本命盤（個人 ACG），也可以是任意時刻（天象地圖）；兩者同一套資料。
 * 座標一律是 (lat, lng) 度數，東經、北緯為正，經度落在 [-180, 180]。
 */
@Serializable
data class AcgMapDto(
  /** 天空的時刻（epoch 毫秒，UTC） */
  val epochMs: Long,
  val basis: AstroGeoBasis,
  /** 每顆星的四條角線；同一 (point, angle) 可能因換日線或極區截斷而分成好幾段 */
  val lines: List<Line>,
  /** 每顆星的天頂點（該星正在頭頂的地點，落在它的 MC 線上） */
  val zeniths: List<Zenith>,
  /** 晨昏線；天空內沒有太陽時為 null */
  val night: Night? = null,
  /** 兩條角線的交點（兩星同時在角上的地點）；呼叫端要求才算 */
  val parans: List<Paran> = emptyList(),
) {

  /** 一個點；JSON 是二元陣列 `[lat, lng]`（兩位小數，無條件捨去）—— 一張圖有幾千個點，物件寫法的 key 佔掉一半的大小 */
  @Serializable(with = PosSerializer::class)
  data class Pos(val lat: Double, val lng: Double)

  @Serializable
  data class Line(val point: AstroPoint, val angle: GeoAngle, val segments: List<List<Pos>>)

  @Serializable
  data class Zenith(val point: AstroPoint, val pos: Pos)

  /**
   * 夜半球：每個經度上，[curve] 那一點到 [nightPole] 之間就是夜裡。
   * [curve] 由西到東排列；[subsolar] 是太陽直射點。
   */
  @Serializable
  data class Night(val subsolar: Pos, val curve: List<Pos>, val nightPole: Int)

  @Serializable
  data class Paran(val point1: AstroPoint, val angle1: GeoAngle, val point2: AstroPoint, val angle2: GeoAngle, val pos: Pos)
}

private fun ILatLng.toPos() = AcgMapDto.Pos(lat.value, lng.value)

/** [AcgMapDto.Pos] ⇄ `[lat, lng]` */
internal object PosSerializer : KSerializer<AcgMapDto.Pos> {
  private val delegate = DoubleArraySerializer()

  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor = SerialDescriptor("destiny.core.astrology.geo.AcgMapDto.Pos", delegate.descriptor)

  override fun serialize(encoder: Encoder, value: AcgMapDto.Pos) =
    encoder.encodeSerializableValue(delegate, doubleArrayOf(value.lat.truncate(2), value.lng.truncate(2)))

  override fun deserialize(decoder: Decoder): AcgMapDto.Pos {
    val a = decoder.decodeSerializableValue(delegate)
    require(a.size == 2) { "Pos 要兩個數：${a.size}" }
    return AcgMapDto.Pos(a[0], a[1])
  }
}

/**
 * 由 context 組出 [AcgMapDto]。
 *
 * @param points 要畫的星，預設 context 內全部
 * @param latStep AS/DS 線的緯度取樣間距（度）
 * @param lngStep 晨昏線的經度取樣間距（度）
 * @param withParans 是否算 parans（兩兩配對 × 角別組合，星多時較慢）
 */
fun AstroGeoContext.toAcgMapDto(
  basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
  points: Collection<AstroPoint> = equatorial.keys,
  latStep: Double = 1.0,
  lngStep: Double = 1.0,
  withParans: Boolean = false,
): AcgMapDto {
  val drawn = points.filter { equatorial[it]?.containsKey(basis) == true }

  val lines = drawn.flatMap { p ->
    GeoAngle.entries.mapNotNull { angle ->
      line(p, angle, basis, latStep)
        .filter { it.isNotEmpty() }
        .takeIf { it.isNotEmpty() }
        ?.let { segs -> AcgMapDto.Line(p, angle, segs.map { seg -> seg.map { it.toPos() } }) }
    }
  }

  val zeniths = drawn.mapNotNull { p -> zenith(p, basis)?.let { AcgMapDto.Zenith(p, it.toPos()) } }

  val night = terminator(lngStep, Planet.SUN)?.let { t ->
    AcgMapDto.Night(t.subsolar.toPos(), t.curve.map { it.toPos() }, t.nightPole.toInt())
  }

  val parans = if (!withParans) emptyList() else buildList {
    drawn.forEachIndexed { i, p1 ->
      drawn.drop(i + 1).forEach { p2 ->
        GeoAngle.entries.forEach { a1 ->
          GeoAngle.entries.forEach { a2 ->
            parans(p1, a1, p2, a2, basis).forEach { add(AcgMapDto.Paran(p1, a1, p2, a2, it.toPos())) }
          }
        }
      }
    }
  }

  return AcgMapDto(gmtJulDay.toEpochMilliseconds(), basis, lines, zeniths, night, parans)
}
