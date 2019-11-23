/**
 * Created by smallufo on 2019-11-23.
 */
package destiny.fengshui.sanyuan

import destiny.core.calendar.ILocation
import destiny.iching.Symbol
import java.time.chrono.ChronoLocalDateTime


interface INineStar {

  enum class Scale {
    YEAR ,
    MONTH
  }

  /** 此時 ([lmt]) 此地 ([loc]) 此方位 ([symbol]) 的年、月、日、時 九星 */
  fun getNineStar(lmt: ChronoLocalDateTime<*>, loc: ILocation , symbol: Symbol) : Map<Scale , NineStar>

  /** 此時 ([lmt]) 此地 ([loc]) 八個方位 的年、月、日、時 九星 */
  fun getNineStar(lmt: ChronoLocalDateTime<*>, loc: ILocation) : List<Pair<Scale , Map<Symbol , NineStar>>>
}
