/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 */
interface IRiseTrans {

  /**
   * 來源、目標時間都是 GMT
   *
   * 根據測試資料 , 美國海軍天文台的計算結果，「似乎」傾向 center = false , refraction = true. 亦即： 計算「邊緣」以及「考量折射」
   *
   * Note : 極區 可能無 rise / set 之值
   */
  fun getGmtTransJulDay(fromGmtJulDay: GmtJulDay,
                        star: Star,
                        point: TransPoint,
                        location: ILocation,
                        transConfig: TransConfig = TransConfig()): GmtJulDay?

}
