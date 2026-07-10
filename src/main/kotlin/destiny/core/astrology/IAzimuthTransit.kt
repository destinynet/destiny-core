/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

/**
 * 逆解：給定星體與「目標地平方位角」，求得當地何時該星體通過此方位角。
 *
 * 方位角採「北=0, 東=90, 南=180, 西=270」慣例 (與 [Azimuth.azimuthDeg] 一致)；
 * swisseph 內部的「南=0」轉換已封裝於底層實作，呼叫端無需處理。
 *
 * 只支援可解析出星體位置者：[Planet]、[Asteroid]、[FixedStar]、[LunarNode]、
 * [LunarApsis]、[Hamburger]。傳入 [Arabic]、[LunarStation] 一律回 null / 空序列。
 *
 * @see IStarTransit 黃道經度逆解
 * @see IRiseTrans 東昇/天頂/西落/天底四角點逆解
 */
interface IAzimuthTransit {

  /**
   * 從 [fromGmt] 起，[forward] 方向找「最近一次」[star] 通過 [targetAzimuthDeg] 的事件。
   *
   * @param targetAzimuthDeg 目標方位角 (北=0 慣例)，會先正規化到 [0, 360)
   * @param aboveHorizonOnly true 時只回傳通過當下高度 > 0 (地平線上/可見) 的事件
   * @return 事件；若星體無法解析、或 [MAX_SCAN_DAYS] 掃描窗內從未通過該方位角、
   *         或 aboveHorizonOnly 過濾後無事件，則回 null
   */
  fun getNextAzimuthTransit(
    star: Star,
    targetAzimuthDeg: Double,
    fromGmt: GmtJulDay,
    loc: ILocation,
    forward: Boolean = true,
    centric: Centric = Centric.GEO,
    aboveHorizonOnly: Boolean = false,
    temperature: Double = 0.0,
    pressure: Double = 1013.25,
    options: StarTypeOptions = StarTypeOptions.MEAN
  ): AzimuthTransit?

  /**
   * [fromGmt]..[toGmt] 區間內，[star] 通過 [targetAzimuthDeg] 的所有事件 (時間遞增)。
   * 預設以 [getNextAzimuthTransit] 迭代組成 (參考 [IStarTransit.getRangeTransitGmt] 慣例)。
   */
  fun getRangeAzimuthTransits(
    star: Star,
    targetAzimuthDeg: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    loc: ILocation,
    centric: Centric = Centric.GEO,
    aboveHorizonOnly: Boolean = false,
    temperature: Double = 0.0,
    pressure: Double = 1013.25,
    options: StarTypeOptions = StarTypeOptions.MEAN
  ): Sequence<AzimuthTransit> {
    return generateSequence(
      getNextAzimuthTransit(star, targetAzimuthDeg, fromGmt, loc, true, centric, aboveHorizonOnly, temperature, pressure, options)
    ) { prev ->
      getNextAzimuthTransit(star, targetAzimuthDeg, prev.gmtJulDay + EPSILON, loc, true, centric, aboveHorizonOnly, temperature, pressure, options)
    }.takeWhile { it.gmtJulDay < toGmt }
  }

  companion object {
    /**
     * 「next」的掃描上限 (天)。方位角約每一恆星日重複，2.0 天足以判定
     * 「可達 → 找到」或「不可達 (循環星/緯度限制) → null」。
     */
    const val MAX_SCAN_DAYS = 2.0

    /** 迭代時跳過已找到事件的最小步長 (juld, 約數秒) */
    const val EPSILON = 0.0001
  }
}
