/**
 * Created by smallufo on 2021-03-11.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/** 七元 */
interface ISevenYuan {

  fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan
}

/**
 * 弘治甲子上元逢，箕井牛柳將符同。
 * 中元虛張室軫宿，下元奎亢胃房求。
 * 甲子己卯符頭定，甲午己酉年值通。
 * 康熙上元畢月值，循環順數去無窮。
 * */
class SevenYuanImpl : ISevenYuan, Serializable {
  override fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan {
    TODO("Not yet implemented")
  }
}
