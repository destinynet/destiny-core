package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay

/**
 * 推運，其實是 『可對應（收斂 , Converge）到某個日期』的推運法。
 * 例如 ProgressionSecondary , ProgressionTertiary , ProgressionMinor , Solar Return , Lunar Return 皆屬此類
 * 而太陽弧 (Solar Arc) 則不屬於此類。因為其星盤並沒有可對應的日期
 */
interface Mappable {

  /**
   * 取得對應的時間 , 通常是收斂到某日期
   *
   * @param natalGmtJulDay 出生時刻
   * @param nowGmtJulDay 欲查閱的時刻 (generally now)
   * @return 「收斂」到的時間
   */
  fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay
}
