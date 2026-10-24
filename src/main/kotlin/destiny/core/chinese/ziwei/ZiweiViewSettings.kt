/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.IntAgeNote
import destiny.core.calendar.eightwords.Direction
import destiny.tools.JSerializable
import destiny.tools.Lang
import destiny.tools.serializers.LangSerializer
import kotlinx.serialization.Serializable

/** 宮干四化「自化」 顯示選項  */
enum class SelfTransFour {

  /** 不顯示  */
  SELF_TRANS_FOUR_NONE,

  /** 文字顯示  */
  SELF_TRANS_FOUR_TEXT,

  /** 箭頭朝外  */
  SELF_TRANS_FOUR_ARROW;
}

/** 宮干四化「化入對宮」的顯示選項  */
enum class OppoTransFour {
  /** 不顯示  */
  OPPO_TRANS_FOUR_NONE,

  /** 朝內(對宮) 箭頭 , 四化星靠近本宮 */
  OPPO_TRANS_FOUR_ARROW_FROM,

  /** 朝內(對宮) 箭頭 , 四化星靠近對宮 */
  OPPO_TRANS_FOUR_ARROW_TO
}

@Serializable
data class ZiweiViewSettings(
  /** 宮干四化「自化」 顯示選項  */
  val selfTransFour: SelfTransFour = SelfTransFour.SELF_TRANS_FOUR_TEXT,

  /** 宮干四化「化入對宮」的顯示選項  */
  val oppoTransFour: OppoTransFour = OppoTransFour.OPPO_TRANS_FOUR_ARROW_TO,

  /** 是否顯示小限  */
  val showSmallRange: Boolean = false,


  /**
   * 是否在紫微盤的**中宮**塞八字，讓懂八字的人合參。
   * 這是「單一張盤面」的畫法；能並排兩張盤的呈現端（八字盤與紫微盤 split screen）可以忽略它與 [direction]。
   */
  val showEightWords: Boolean = true,

  /** 中宮八字的排列，右至左 or 左至右（只在 [showEightWords] 時有意義） */
  val direction: Direction = Direction.R2L,

  /**
   * 顯示雜曜（乙級星 [StarMinor]）。
   *
   * 以下五個 `show*` 是**純呈現**：應讓 [IZiweiConfig.stars] 保持全算，畫的時候才依這裡過濾 ——
   * 若用這幾個開關去縮減 `stars`，「關掉顯示」就變成換了一張盤（計算層 config 不同）。
   * 六吉六煞沒有開關，一律顯示。
   */
  val showMinors: Boolean = true,

  /** 顯示博士12神煞  */
  val showDoctors: Boolean = true,

  /** 顯示長生12神煞  */
  val showLongevity: Boolean = true,

  /** 顯示 將前12星  */
  val showGeneralFront: Boolean = true,

  /** 顯示 歲前12星  */
  val showYearFront: Boolean = true,

  /** 歲運註記 */
  val ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),

  /** 顯示語言 */
  @Serializable(with = LangSerializer::class)
  val locale: Lang = Lang.DEFAULT

) : JSerializable
