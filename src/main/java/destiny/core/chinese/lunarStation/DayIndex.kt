/**
 * Created by smallufo on 2021-03-20.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.虛
import destiny.core.chinese.StemBranch
import kotlinx.serialization.Serializable

/** 0 .. 419 */
@JvmInline
@Serializable
value class DayIndex(val value: Int) {

  constructor(yuan: Int, day: StemBranch) : this((yuan - 1) * 60 + day.getAheadOf(StemBranch.甲子))

  init {
    require(value >= 0)
    require(value < 420)
  }

  /** 1 .. 7 */
  fun yuan(): Int = (value / 60) + 1

  /** 將 , 1 .. 4 */
  fun leaderIndex(): Int = (value - (yuan() - 1) * 60) / 15 + 1

  /** 將星 */
  fun leader(): LunarStation = 虛.next((yuan() - 1) * 60 + (leaderIndex() - 1) * 15)

  /** 日禽 */
  fun station(): LunarStation = 虛.next(value)
}
