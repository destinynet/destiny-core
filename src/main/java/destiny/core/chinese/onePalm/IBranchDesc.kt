/**
 * Created by smallufo on 2015-05-22.
 */
package destiny.core.chinese.onePalm

import destiny.core.chinese.Branch

/** 由地支，取得解釋  */
interface IBranchDesc {

  /** 取得某宮位的簡介  */
  fun getHouseIntro(house: Branch): String

  /** 取得某柱（座落於某宮）的解釋  */
  fun getContent(pillar: IPalmModel.Pillar, house: Branch): String

  /** 取得「時柱」的詩詞  */
  fun getPoem(hourBranch: Branch): String

  /** 取得「時柱」的解釋  */
  fun getContent(hourBranch: Branch): String
}
