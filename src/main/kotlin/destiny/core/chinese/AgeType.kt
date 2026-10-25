/**
 * Created by smallufo on 2022-06-04.
 */
package destiny.core.chinese


enum class AgeType(
  /** 出生那一年算幾歲：實歲 0、虛歲 1。歲數 → 西元年：`birthYear + age - birthYearAge` */
  val birthYearAge: Int,
) {
  REAL(0),
  VIRTUAL(1);

  /** 歲數是出生後第幾年（出生那年＝0） */
  fun yearsAfterBirth(age: Int): Int = age - birthYearAge
}
