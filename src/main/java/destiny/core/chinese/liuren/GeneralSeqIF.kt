/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.Descriptive

interface GeneralSeqIF : Descriptive {

  /** 取得下 n 個天將 , n 若等於 0  , 則傳回自己  */
  fun next(from: General, n: Int): General

  fun prev(from: General, n: Int): General {
    return next(from, 0 - n)
  }
}
