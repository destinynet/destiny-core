/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.虛
import destiny.core.chinese.StemBranch


/**
 * @param value : 0..419
 * @param epoch : 內定為 1564 一元甲子虛 , 另有可能設定為 1864 一元甲子虛
 */
data class YearIndex(val value: Int, val epoch: Int) {

  constructor(yuan: Int, year: StemBranch, epoch: Int) : this((yuan - 1) * 60 + year.getAheadOf(StemBranch.甲子), epoch)

  init {
    require(value in 0..419) { "invalid value : $value " }
    require(epoch == 1564 || epoch == 1864) { "Unsupported epoch = $epoch" }
  }

  /** 1 .. 7 */
  val yuan: Int = (value / 60) + 1

  /** 年禽 */
  val station: LunarStation = 虛.next(value)
}
