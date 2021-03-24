/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import java.io.Serializable


interface ILunarStationContextModel {
  val eightwords: IEightWords

  val year: LunarStation
  val month: LunarStation
  val day: LunarStation
  val hour: LunarStation

  fun getStation(scale: Scale) : LunarStation {
    return when(scale) {
      Scale.YEAR -> year
      Scale.MONTH -> month
      Scale.DAY -> day
      Scale.HOUR -> hour
    }
  }

  /** 翻禽（彼禽） */
  val oppo: LunarStation

  /** 活曜（我禽） */
  val self: LunarStation

  val oppoHouseMap: Map<Branch, OppoHouse>
  val selfHouseMap: Map<Branch, SelfHouse>

  /** 暗金伏斷 */
  val hiddenVenusFoe: Set<Pair<Scale, Scale>>


}

data class ContextModel(override val eightwords: IEightWords,
                        override val year: LunarStation,
                        override val month: LunarStation,
                        override val day: LunarStation,
                        override val hour: LunarStation,
                        override val oppo: LunarStation,
                        override val self: LunarStation,
                        override val oppoHouseMap: Map<Branch, OppoHouse>,
                        override val selfHouseMap: Map<Branch, SelfHouse>,
                        override val hiddenVenusFoe: Set<Pair<Scale, Scale>>) : ILunarStationContextModel, Serializable
