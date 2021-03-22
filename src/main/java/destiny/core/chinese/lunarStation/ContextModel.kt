/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.chinese.Branch
import java.io.Serializable


interface ILunarStationContextModel {
  val year: LunarStation
  val month: LunarStation
  val day: LunarStation
  val hour: LunarStation

  /** 翻禽（彼禽） */
  val oppo: LunarStation
  val oppoHouseMap: Map<Branch, OppoHouse>
    get() {
      oppo.planet.let {  }
      return emptyMap()
    }

  /** 活曜（我禽） */
  val self: LunarStation

  /** 暗金伏斷 */
  val hiddenVenusFoe: Set<Pair<Scale, Scale>>


}

data class ContextModel(override val year: LunarStation,
                        override val month: LunarStation,
                        override val day: LunarStation,
                        override val hour: LunarStation,
                        override val oppo: LunarStation,
                        override val self: LunarStation,
                        override val hiddenVenusFoe: Set<Pair<Scale, Scale>>) : ILunarStationContextModel, Serializable
