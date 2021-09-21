/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import java.util.*


/**
 * 取「月將」 (不是月令干支！)
 * 一般而言就是太陽星座（過中氣）
 * 但是有些人堅持用「月支相合」（過節）
 * 因此會產生兩種實作
 */
enum class MonthMaster {
  /** 月支六合（過節） */
  Combined,

  /** 星體觀測（過中氣） */
  StarPosition
}

fun MonthMaster.asDescriptive() = object : Descriptive {

  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      MonthMaster.Combined     -> "月支六合（過節）"
      MonthMaster.StarPosition -> "星體觀測（過中氣）"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      MonthMaster.Combined     -> "純粹以八字月支六合取月將。"
      MonthMaster.StarPosition -> "真實觀測太陽在黃道的度數，判斷月將（太陽星座）。"
    }
  }
}
