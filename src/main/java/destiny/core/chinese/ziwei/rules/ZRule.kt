/**
 * Created by smallufo on 2022-06-07.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.IPlate
import destiny.core.chinese.ziwei.IZiweiFeature
import destiny.core.chinese.ziwei.ZiweiConfig
import java.time.chrono.ChronoLocalDateTime


interface ZRule<T> {
  
  val name : String
    get() = javaClass.simpleName

  fun IZiweiFeature.test(plate: IPlate, lmt : ChronoLocalDateTime<*>, config: ZiweiConfig): T
}
