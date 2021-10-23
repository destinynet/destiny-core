/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.eightwords.Direction
import java.io.Serializable

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

data class ViewSettings(
  /** 宮干四化「自化」 顯示選項  */
  val selfTransFour: SelfTransFour = SelfTransFour.SELF_TRANS_FOUR_TEXT,

  /** 宮干四化「化入對宮」的顯示選項  */
  val oppoTransFour: OppoTransFour = OppoTransFour.OPPO_TRANS_FOUR_ARROW_TO,

  /** 是否顯示小限  */
  val showSmallRange: Boolean = false,


  /** TODO : 是否顯示八字盤 , 應該與 [direction] 整合 */
  val showEightWords: Boolean = true,

  /** 八字排盤，右至左 or 左至右 */
  val direction: Direction = Direction.R2L,

  /** 顯示雜曜  */
  val showMinors: Boolean = true,

  /** 顯示博士12神煞  */
  val showDoctors: Boolean = true,

  /** 顯示長生12神煞  */
  val showLongevity: Boolean = true,

  /** 顯示 將前12星  */
  val showGeneralFront: Boolean = true,

  /** 顯示 歲前12星  */
  val showYearFront: Boolean = true

) : Serializable

