/**
 * @author smallufo
 * Created on 2010/9/13 at 上午2:39:23
 */
package destiny.core.oracles.linchi

import destiny.core.iching.Symbol

import java.io.Serializable

/** 靈棋經  */
data class Linchi(
  /** 第幾卦 */
  val index: Int,
  /** 卦名： ex "大通卦" , 含「卦」總共三個字  */
  val name: String,
  /** 幾上幾中幾下 , 以三個數字表示 */
  val key: String,
  /** 什麼「象」 , ex "昇騰之象"  */
  val image: String,
  /** 轉成一個 Symbol , 例如「乾天西北」,「離火正南」 ...  */
  val symbol: Symbol,
  /** 卦象解釋，四個中文字 , ex "純陽得令"  */
  val symbolDesc: String) : Serializable
