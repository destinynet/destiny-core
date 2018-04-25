/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.chinese.StemBranch

import java.io.Serializable

/** 一柱大運的相關資訊。包含上方文字，干支...  */
data class FortuneData(
  /** 大運干支  */
  val stemBranch: StemBranch,
  /** 起運時刻  */
  val startFortuneGmtJulDay: Double,
  /** 終運時刻  */
  val endFortuneGmtJulDay: Double,
  /** 起運歲數 (可能是虛歲、或是實歲)  */
  val startFortuneAge: Int,
  /** 終運歲數 (可能是虛歲、或是實歲)  */
  val endFortuneAge: Int,
  /** 起運歲數的註解（西元、或民國） */
  val startFortuneAgeNotes: List<String>,
  /** 終運歲數的註解（西元、或民國） */
  val endFortuneAgeNotes: List<String>) : Serializable {

  override fun toString(): String {
    return "{$startFortuneAge $stemBranch}"
  }
} // FortuneData
