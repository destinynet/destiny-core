/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese

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
