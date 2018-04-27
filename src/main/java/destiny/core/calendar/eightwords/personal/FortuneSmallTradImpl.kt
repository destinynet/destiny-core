/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 以時柱推算小運
 * 陽男陰女順推 , 反之逆推
 * 時柱自己本身不算
 */
class FortuneSmallTradImpl(private val fortuneDirectionImpl: IFortuneDirection) : IPersonFortuneSmall , Serializable {
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}