/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.IStemBranch
import jakarta.inject.Named
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 傳統八字起大運法
 * 每條大運，都是固定 10 年
 *
 * 參考
 * https://sites.google.com/site/laughing8word/home/horoscope_figure
 */
@Named
class FortuneLargeTradImpl : IPersonFortuneLarge , Serializable {

  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, count: Int, config: FortuneLargeConfig): List<FortuneData> {
    TODO("Not yet implemented")
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, targetGmt: ChronoLocalDateTime<*>, config: FortuneLargeConfig): IStemBranch? {
    TODO("Not yet implemented")
  }

}
