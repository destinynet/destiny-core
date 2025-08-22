/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.*
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.toLmt
import destiny.tools.KotlinLogging
import java.io.Serializable

/**
 * [validFrom] , [validTo] : 此 return context 有效期限
 * 若為 converse return , [validFrom] 則會 after [validTo]
 */
data class ReturnModel(val horoscope: IHoroscopeModel, val validFrom: GmtJulDay, val validTo: GmtJulDay)

interface IReturnContext : Conversable, IDiscrete {

  val planet: Planet

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  val orb: Double

  /** 是否消除歲差， false = 不計算歲差  */
  val precession: Boolean

  /** 對外主要的 method , 取得 return 盤  */
  fun getReturnHoroscope(natalModel: IHoroscopeModel, nowGmtJulDay: GmtJulDay, nowLoc: ILocation, nowPlace: String? = null): ReturnModel

  fun IHoroscopeModel.getReturnDto(
    grain: BirthDataGrain,
    nowGmtJulDay: GmtJulDay,
    nowLoc: ILocation,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    nowPlace: String?,
    threshold: Double?,
    includeClassical: Boolean
  ): IReturnDto

  fun getRangedReturns(
    model: IPersonHoroscopeModel, grain: BirthDataGrain,
    fromTime: GmtJulDay, toTime: GmtJulDay,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    threshold: Double?,
    returnChartIncludeClassical: Boolean
  ): List<IReturnDto> {
    return getReturns(
      model , grain, listOf(Pair(fromTime, toTime)) , aspectEffective, aspectCalculator, config, threshold, returnChartIncludeClassical
    )
  }

  /**
   * 取得涵蓋多個「不連續」時間區間所需的所有返照圖，高效能，避免重複計算
   */
  fun getReturns(
    model: IPersonHoroscopeModel, grain: BirthDataGrain,
    periods: List<Pair<GmtJulDay, GmtJulDay>>,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    threshold: Double?,
    returnChartIncludeClassical: Boolean
  ): List<IReturnDto> {
    if (periods.isEmpty()) {
      return emptyList()
    }

    // 確保 periods 按時間順序處理，這對於 fold 邏輯至關重要
    val sortedPeriods = if (forward) {
      periods.sortedBy { it.first }
    } else {
      periods.sortedByDescending { it.first }
    }


    // 使用 fold 建立 SR 集合。acc (accumulator) 是至今為止找到的所有 SR 的集合。
    // 我們使用 LinkedHashSet 來保持插入順序並自動處理重複。
    return sortedPeriods.fold(LinkedHashSet<IReturnDto>()) { acc, (periodFrom, periodTo) ->
      // 取得累積集合中最後一個 SR。因為 group 已排序且我們用 LinkedHashSet，
      // acc.lastOrNull() 會是時間上最晚的 SR。
      var currentSr = if (forward) acc.lastOrNull() else acc.firstOrNull()

      // 如果沒有已知的 SR，或者已知的最晚 SR 沒有涵蓋到當前 group 的起始點，
      // 我們才需要計算一個新的 SR。
      val needsNewInitialSr = when {
        currentSr == null -> true
        forward           -> periodFrom > currentSr.validTo
        else              -> periodFrom < currentSr.validTo // converse
      }

      if (needsNewInitialSr) {
        currentSr = model.getReturnDto(
          grain, periodFrom,
          model.location,
          aspectEffective, aspectCalculator, config,
          model.place, threshold, returnChartIncludeClassical
        )
      }

      // 從 currentSr 開始，產生一個 SR 序列，直到完全覆蓋當前 group 的時間範圍。
      generateSequence(currentSr) { previousSr ->
        val shouldContinue = if (forward) {
          previousSr.validTo < periodTo
        } else {
          previousSr.validTo > periodTo // converse
        }

        if (!shouldContinue) {
          null // 如果前一個 SR 已覆蓋 group，則序列結束。
        } else {
          // 否則，計算下一個 SR。
          val nextSrStartTime = if (forward) previousSr.validTo + 1.0 else previousSr.validTo - 1.0
          model.getReturnDto(
            grain, nextSrStartTime,
            model.location,
            aspectEffective, aspectCalculator, config,
            model.place, threshold, returnChartIncludeClassical
          )
        }
      }.forEach { sr ->
        // 將序列中的每個 SR 加入到累積器中。
        // 如果 sr 已存在，Set 的 add 方法會自動忽略，從而避免重複計算和儲存。
        acc.add(sr)
      }
      acc // 回傳更新後的累積器，用於下一次迭代。
    }.sortedBy { if (forward) it.validFrom else it.validTo } // 最後，將集合排序並轉為 List。
  }
}


/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  override val planet: Planet = Planet.SUN,
  /** 計算星體的介面  */
  private val starPosImpl: IStarPosition<*>,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private val starTransitImpl: IStarTransit,
  private val horoscopeFeature: IHoroscopeFeature,
  private val dtoFactory: DtoFactory,
  /** 是否順推 , true 代表順推 , false 則為逆推 */
  override val forward: Boolean = true,

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  override val orb: Double = 0.0,
  /** 是否消除歲差，內定是不計算歲差  */
  override val precession: Boolean = false
) : IReturnContext, Serializable {

  override fun getReturnHoroscope(natalModel: IHoroscopeModel, nowGmtJulDay: GmtJulDay, nowLoc: ILocation, nowPlace: String?): ReturnModel {
    return getConvergentPeriod(natalModel.gmtJulDay, nowGmtJulDay).let { (from, to) ->

      val config = HoroscopeConfig(
        natalModel.points,
        HouseSystem.PLACIDUS,
        Coordinate.ECLIPTIC,
        Centric.GEO,
        0.0,
        1013.25,
        VoidCourseImpl.Medieval,
        nowPlace ?: natalModel.place,
      )
      val horoscope = horoscopeFeature.getModel(from, nowLoc, config)
      ReturnModel(horoscope, from, to)
    }
  }


  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    // 先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree: ZodiacDegree = starPosImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lngDeg

    val delta = 0.001

    return if (forward) {
      // 從現在時刻 順推 , 取得 planet 回歸到 natal 度數(plus orb) 的時刻
      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay + delta, false, coordinate) //false 代表逆推，往before算
    } else {
      // 逆推
      // 1. 先找到 nowGmtJulDay 當下所屬的「順行」返照盤的起始時間
      val currentForwardReturnStart = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, false, coordinate)

      // 2. 計算該返照盤相對於本命盤的年齡差距 (duration)
      val ageDuration = currentForwardReturnStart - natalGmtJulDay

      // 3. 從本命時間往前推此 duration，得到逆行返照的搜尋基準點
      val converseSearchBaseGmt = natalGmtJulDay - ageDuration

      // 4. 從此基準點往前找，即可找到正確的逆行返照時間點
      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), converseSearchBaseGmt + delta, false, coordinate)
    }
  }

  override fun getConvergentPeriod(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): Pair<GmtJulDay, GmtJulDay> {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    // 先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree: ZodiacDegree = starPosImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lngDeg
    val delta = 0.1

    // 再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (forward) {
      // 順推
      val from = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, false, coordinate)
      val to = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, true, coordinate)
      from to to
    } else {
      // 1. 先找到 nowGmtJulDay 當下所屬的「順行」返照盤的起始時間
      val currentForwardReturnStart = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay , false, coordinate)
      // 2. 計算該返照盤相對於本命盤的年齡差距 (duration)
      val ageDuration = currentForwardReturnStart - natalGmtJulDay
      // 3. 從本命時間往前推此 duration，得到逆行返照的搜尋基準點
      val converseSearchBaseGmt = natalGmtJulDay - ageDuration

      // 4. 從此基準點分別往「未來」和「過去」找，以確定週期的兩端
      //   注意：逆行返照的 from (有效期的結束) 時間會比 to (有效期的開始) 晚
      val from = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), converseSearchBaseGmt - delta, true, coordinate)
      val to = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), converseSearchBaseGmt - delta, false, coordinate)

      from to to
    }
  }

  override fun IHoroscopeModel.getReturnDto(
    grain: BirthDataGrain,
    nowGmtJulDay: GmtJulDay,
    nowLoc: ILocation,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    nowPlace: String?,
    threshold: Double?,
    includeClassical: Boolean
  ): IReturnDto {
    logger.debug { "[$planet] getReturnDto , nowGmtJulDay = $nowGmtJulDay (${nowGmtJulDay.toLmt(this.location, JulDayResolver1582CutoverImpl())})" }
    val returnModel: ReturnModel = getReturnHoroscope(this, nowGmtJulDay, nowLoc, nowPlace)

    val innerIncludeHouse = (grain == BirthDataGrain.MINUTE)

    val returnChart: IHoroscopeDto = with(dtoFactory) {
      returnModel.horoscope.toHoroscopeDto(grain, rulerImpl, aspectEffective, aspectCalculator, config, includeClassical)
    }.let { it as HoroscopeDto }

      // 移除以下 fields，畢竟這在 return chart 參考度不高
      .copy(
        // 移除 中點資訊
        midPoints = emptyList(),
        // 移除 星體資訊
        //stars = emptyMap(),
        // 移除 星座資訊
        //signs = emptyMap(),
        // 移除 宮位資訊
        //houses = emptyList()
      )

    val synastry: Synastry = horoscopeFeature.synastry(returnModel.horoscope, this, aspectCalculator, threshold, innerIncludeHouse).let { synastry: Synastry ->
      when (grain) {
        BirthDataGrain.MINUTE -> synastry
        BirthDataGrain.DAY    -> {
          val aspects = synastry.aspects.filterNot { aspect -> aspect.points.any { it is Axis } }
            .map { sa: SynastryAspect ->
              sa.copy(outerPointHouse = null, innerPointHouse = null)
            }
          Synastry(aspects, emptyMap())
        }
      }
    }

    return when (this@ReturnContext.planet) {
      Planet.SUN -> ReturnDto(ReturnType.SOLAR, returnChart, synastry, returnModel.validFrom, returnModel.validTo)
      Planet.MOON -> ReturnDto(ReturnType.LUNAR, returnChart, synastry, returnModel.validFrom, returnModel.validTo)
      else -> throw IllegalArgumentException("Unsupported planet: $planet")
    }
  }


  companion object {
    private val rulerImpl: IRuler = RulerPtolemyImpl
    val logger = KotlinLogging.logger { }
  }

}
