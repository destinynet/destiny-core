/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.calendar.eightwords.Direction
import java.io.Serializable
import java.util.*

data class ViewSettings(
  /** 宮干四化「自化」 顯示選項  */
  val selfTransFour: SelfTransFour = SelfTransFour.SELF_TRANS_FOUR_TEXT,

  /** 宮干四化「化入對宮」的顯示選項  */
  val oppoTransFour: OppoTransFour = OppoTransFour.OPPO_TRANS_FOUR_ARROW,

  /** 是否顯示小限  */
  val showSmallRange: Boolean = false,

  /** 是否顯示八字盤  */
  val showEightWords: Boolean = true,

  /** 八字排盤，右至左 or 左至右. 若是不顯示的話，則為 null */
  val direction: Direction? = Direction.R2L,

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

  ) : Serializable {
  /** 宮干四化「自化」 顯示選項  */
  enum class SelfTransFour : Descriptive {

    /** 不顯示  */
    SELF_TRANS_FOUR_NONE,

    /** 文字顯示  */
    SELF_TRANS_FOUR_TEXT,

    /** 箭頭朝外  */
    SELF_TRANS_FOUR_ARROW;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(ViewSettings::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }

  /** 宮干四化「化入對宮」的顯示選項  */
  enum class OppoTransFour : Descriptive {
    /** 不顯示  */
    OPPO_TRANS_FOUR_NONE,

    /** 朝內(對宮) 箭頭  */
    OPPO_TRANS_FOUR_ARROW;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(ViewSettings::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }
} // data class ViewSettings
