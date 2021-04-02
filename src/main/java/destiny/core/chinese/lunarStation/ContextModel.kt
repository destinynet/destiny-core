/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Gender
import destiny.core.ITimeLoc
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
      Scale.YEAR -> year
      Scale.MONTH -> month
      Scale.DAY -> day
      Scale.HOUR -> hour
    }
  }

  /** 翻禽（彼禽） */
  val oppo: LunarStation

  /** 翻禽（彼禽）所在宮位 */
  val oppoHouse: OppoHouse

  /** 活曜（我禽） */
  val self: LunarStation

  /** 活曜（我禽）所在宮位 */
  val selfHouse: SelfHouse

  /** 暗金伏斷 */
  val hiddenVenusFoe: Set<Pair<Scale, Scale>>

  /** 四課三傳 */
//  fun getDigestCanvas(desiredLocale: Locale): ColorCanvas {
//    val locale = LocaleTools.getBestMatchingLocale(
//      desiredLocale,
//      listOf(
//        Locale.TRADITIONAL_CHINESE,
//        Locale.SIMPLIFIED_CHINESE
//      )
//    ) ?: Locale.TRADITIONAL_CHINESE
//
//    return ColorCanvas(4, 10, ChineseStringTools.NULL_CHAR).apply {
//      // 左上：活曜
//      setText(self.toString(locale), 3, 1, "green")
//      // 左下：時禽
//      setText(hour.toString(locale), 4, 1)
//
//      // 左中上：翻禽
//      setText(oppo.toString(locale), 3, 3, "red")
//      // 左中下：日禽
//      setText(day.toString(locale), 4, 3)
//
//      // 右中上：時禽
//      setText(hour.toString(locale), 3, 7)
//      // 又中下：月禽
//      setText(month.toString(locale), 4, 7)
//
//      // 右上：日禽
//      setText(day.toString(locale), 3, 9)
//      // 右下：年禽
//      setText(year.toString(locale), 4, 9)
//
//      // 初傳：日禽
//      setText(day.toString(locale), 1, 5)
//      // 中傳：時禽
//      setText(hour.toString(locale), 2, 5)
//      // 末傳：翻禽
//      setText(oppo.toString(locale), 3, 5)
//    }
//  }
}

data class ContextModel(override val eightwords: IEightWords,
                        override val year: LunarStation,
                        override val month: LunarStation,
                        override val day: LunarStation,
                        override val hour: LunarStation,
                        override val oppo: LunarStation,
                        override val oppoHouse: OppoHouse,
                        override val self: LunarStation,
                        override val selfHouse: SelfHouse,
                        override val hiddenVenusFoe: Set<Pair<Scale, Scale>>) : IContextModel, Serializable

interface IModernContextModel : IContextModel {

  val gender: Gender

  val created: LocalDateTime

  val timeLoc: ITimeLoc

  val place: String?

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
                              override val gender: Gender,
                              override val created: LocalDateTime,
                              override val timeLoc: ITimeLoc,
                              override val place: String?,
                              override val method: IModernContextModel.Method,
                              override val question: String?) : IModernContextModel, IContextModel by contextModel

