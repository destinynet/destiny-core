/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.IStarPositionWithAzimuthCalculator
import destiny.core.astrology.Star
import destiny.core.astrology.TransPoint
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.fixError
import destiny.core.calendar.toLmt
import destiny.tools.location.IStaticMap
import destiny.tools.location.MapType
import jakarta.inject.Named
import java.time.LocalDateTime
import java.util.*

/**
 * 組裝 [StarMountainDto]：星曆計算（samples/transits/markers）全在伺服器，
 * 地圖 PNG 經 [IStaticMap] 取得後以 base64 data-url 內嵌，前端只做內插與繪製。
 */
@Named
class StarMountainDtoFactory(
  private val starMountainService: StarMountainService,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
  private val riseTransImpl: IRiseTrans,
  private val staticMap: IStaticMap,
  private val julDayResolver: JulDayResolver,
) {

  suspend fun getStarMountainDto(
    star: Star,
    fromLmt: LocalDateTime,
    toLmt: LocalDateTime,
    loc: ILocation,
    place: String? = null,
    mapZoom: Int = DEFAULT_MAP_ZOOM,
    mapScale: Int = DEFAULT_MAP_SCALE,
    sampleStepMinutes: Double = DEFAULT_SAMPLE_STEP_MINUTES,
    locale: Locale = Locale.getDefault(),
  ): StarMountainDto {
    require(fromLmt < toLmt) { "fromLmt ($fromLmt) must precede toLmt ($toLmt)" }

    val fromGmt = fromLmt.toGmtJulDay(loc)
    val toGmt = toLmt.toGmtJulDay(loc)
    val totalMin = (toGmt - fromGmt) * 1440.0

    fun GmtJulDay.toTMin(): Double = (this - fromGmt) * 1440.0
    fun GmtJulDay.toLmtTime(): LocalDateTime = this.toLmt(loc, julDayResolver).fixError() as LocalDateTime

    // 星體位置密集取樣（步長 sampleStepMinutes，含區間終點）
    val samples = buildList {
      var t = 0.0
      while (t < totalMin) {
        add(t)
        t += sampleStepMinutes
      }
      add(totalMin)
    }.map { tMin ->
      val pos = starPosWithAzimuth.getPositionWithAzimuth(star, fromGmt + tMin / 1440.0, loc, Centric.GEO, Coordinate.ECLIPTIC)
      StarMountainDto.Sample(tMin, pos.azimuthDeg, pos.apparentAltitude)
    }

    // 三盤各一份「進入新山」事件
    val transits = StarMountainDto.Plate.entries.map { plate ->
      val entries = starMountainService.getMountainTransits(star, fromGmt, toGmt, loc, plate.compass()).map { e ->
        StarMountainDto.Entry(e.mountain, e.gmtJulDay.toLmtTime(), e.gmtJulDay.toTMin(), e.azimuthDeg, e.isRetreat)
      }
      StarMountainDto.PlateTransits(plate, entries)
    }

    // 升/中天/落/天底 標記（range 內可能各有多筆，如多日 range）
    val markers = TransPoint.entries.flatMap { tp ->
      buildList {
        var cursor = fromGmt
        while (true) {
          val t = riseTransImpl.getGmtTransJulDay(cursor, star, tp, loc) ?: break
          if (t > toGmt) break
          add(StarMountainDto.EventMarker(tp, t.toLmtTime(), t.toTMin()))
          cursor = t + MARKER_ADVANCE
        }
      }
    }.sortedBy { it.tMin }

    val png = staticMap.getImage(loc, width = MAP_SIZE, height = MAP_SIZE, zoom = mapZoom, mapType = MapType.roadmap, scale = mapScale, locale = locale)
    val mapImageDataUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(png)

    return StarMountainDto(
      location = loc,
      place = place,
      gmtOffsetMinutes = TimeTools.getOffset(fromLmt, loc).inWholeMinutes.toInt(),
      star = star,
      fromLmt = fromLmt,
      toLmt = toLmt,
      mapImageDataUrl = mapImageDataUrl,
      mapZoom = mapZoom,
      mapScale = mapScale,
      samples = samples,
      transits = transits,
      markers = markers,
    )
  }

  private fun StarMountainDto.Plate.compass(): AbstractMountainCompass = when (this) {
    StarMountainDto.Plate.EARTHLY  -> EarthlyCompass()
    StarMountainDto.Plate.HUMAN    -> HumanCompass()
    StarMountainDto.Plate.HEAVENLY -> HeavenlyCompass()
  }

  companion object {
    /** 動畫平滑度：一日 720 點左右已足（近中天的精確時刻靠 transits，非內插） */
    const val DEFAULT_SAMPLE_STEP_MINUTES = 2.0
    const val DEFAULT_MAP_ZOOM = 15
    const val DEFAULT_MAP_SCALE = 2
    private const val MAP_SIZE = 640

    /** 同一 [TransPoint] 找下一筆事件時，跳過剛找到那筆的前進量 (天)：1 分鐘 */
    private const val MARKER_ADVANCE = 1.0 / 1440.0
  }
}
