/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.IBirthDataNamePlace
import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.calendar.eightwords.IEightWords
import java.io.Serializable
import java.time.LocalDateTime


interface IContextModel : Serializable {
  val eightwords: IEightWords

  val year: LunarStation
  val month: LunarStation
  val day: LunarStation
  val hour: LunarStation

  fun getStation(scale: Scale): LunarStation {
    return when (scale) {
      Scale.YEAR  -> year
      Scale.MONTH -> month
      Scale.DAY   -> day
      Scale.HOUR  -> hour
    }
  }

  /** [DayIndex] */
  val dayIndex: DayIndex

  /** 翻禽（彼禽） */
  val oppo: LunarStation

  /** 翻禽（彼禽）所在宮位 */
  val oppoHouse: OppoHouse

  /** 活曜（我禽） */
  val self: LunarStation

  /** 活曜（我禽）所在宮位 */
  val selfHouse: SelfHouse

  /** 倒將 (我正將) */
  val reversed: LunarStation

  /** 暗金伏斷 */
  val hiddenVenusFoes: Set<Pair<Scale, Scale>>

}

@kotlinx.serialization.Serializable
data class ContextModel(override val eightwords: IEightWords,
                        override val year: LunarStation,
                        override val month: LunarStation,
                        override val day: LunarStation,
                        override val hour: LunarStation,
                        override val dayIndex: DayIndex,
                        override val oppo: LunarStation,
                        override val oppoHouse: OppoHouse,
                        override val self: LunarStation,
                        override val selfHouse: SelfHouse,
                        override val reversed: LunarStation,
                        override val hiddenVenusFoes: Set<Pair<Scale, Scale>>) : IContextModel, Serializable

interface IModernContextModel : IContextModel, IBirthDataNamePlace {

  val created: LocalDateTime

  enum class Method {
    /** 當下時間 [created] 排盤 */
    NOW,

    /** 指定時間 (maybe 出生盤) */
    SPECIFIED,

    /** 今日抽時 */
    RANDOM_HOUR,

    /** 隨機時刻 */
    RANDOM_TIME
  }

  val method: Method

  val question: String?
}

data class ModernContextModel(val contextModel: IContextModel,
                              val bdnp: IBirthDataNamePlace,
                              override val created: LocalDateTime,
                              override val method: IModernContextModel.Method,
                              override val question: String?) : IModernContextModel, IContextModel by contextModel, IBirthDataNamePlace by bdnp

