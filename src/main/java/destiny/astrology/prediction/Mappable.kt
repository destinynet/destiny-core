package destiny.astrology.prediction

import java.time.chrono.ChronoLocalDateTime

/**
 * 推運，其實是 『可對應（收斂 , Converge）到某個日期』的推運法。<BR></BR>
 * 例如 ProgressionSecondary , ProgressionTertiary , ProgressionMinor , Solar Return , Lunar Return 皆屬此類 <BR></BR>
 * 而太陽弧 (Solar Arc) 則不屬於此類。因為其星盤並沒有可對應的日期
 */
interface Mappable {
  /**
   * 取得對應的時間 , 通常是收斂到某日期
   * @param natalTime 通常是出生時間
   * @param nowTime   通常是現在時間
   * @return 「收斂」到的時間
   */
  fun getConvergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>): ChronoLocalDateTime<*>

}
