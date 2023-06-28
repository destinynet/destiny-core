/**
 * Created by smallufo on 2019-11-30.
 */
package destiny.core.chinese

import java.io.Serializable


interface IStemBranch2 : Serializable {

  enum class Constraint {
    NORMAL, // 標準 60 甲子
    NULLABLE, // 任何一個可為 null
    UNCONSTRAINED, // 120甲子
  }

  val stem: Stem?
  val branch: Branch?

  val constraints: Set<Constraint>
    get() {
      return mutableSetOf<Constraint>().apply {
        if (stem == null || branch == null) {
          add(Constraint.NULLABLE)
        }

        if (stem != null && branch != null) {
          if ( stem!!.index % 2 != branch!!.index % 2) {
            add(Constraint.UNCONSTRAINED)
          } else {
            add(Constraint.NORMAL)
          }
        }
      }.toSet()
    }
}
