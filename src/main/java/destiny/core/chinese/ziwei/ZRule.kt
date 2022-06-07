/**
 * Created by smallufo on 2022-06-07.
 */
package destiny.core.chinese.ziwei

import java.time.chrono.ChronoLocalDateTime


interface ZRule<T> {

  fun IZiweiFeature.test(plate: IPlate , lmt : ChronoLocalDateTime<*> , config: ZiweiConfig): T
}
