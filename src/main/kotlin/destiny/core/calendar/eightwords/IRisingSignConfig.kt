/**
 * Created by smallufo on 2023-07-22.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IHouseConfig


interface IRisingSignConfig : IHouseConfig {

  var tradChineseRisingSignConfig: TradChineseRisingSignConfig
  var risingSignImpl: RisingSignImpl

  val risingSignConfig : RisingSignConfig
    get() = RisingSignConfig(houseConfig, tradChineseRisingSignConfig, risingSignImpl)
}
